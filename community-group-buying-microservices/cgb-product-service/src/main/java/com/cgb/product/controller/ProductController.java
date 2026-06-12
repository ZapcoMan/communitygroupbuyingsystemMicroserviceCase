package com.cgb.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.common.annotation.RateLimit;
import com.cgb.product.entity.ProductEntity;
import com.cgb.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品管理")
@RestController
@RequestMapping("/shangpin")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService shangpinService;

    @Operation(summary = "发布商品")
    @PostMapping
    @RateLimit(key = "product_create", count = 10, period = 1, unit = RateLimit.TimeUnit.MINUTES)
    public R<?> save(@RequestBody ProductEntity entity, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        entity.setMerchantId(userId);
        shangpinService.save(entity);
        return R.ok("发布成功");
    }

    @Operation(summary = "商品列表")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) ProductEntity params) {
        IPage<ProductEntity> result = shangpinService.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(shangpinService.getById(id));
    }

    @Operation(summary = "修改商品")
    @PutMapping
    public R<?> update(@RequestBody ProductEntity entity) {
        shangpinService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        shangpinService.delete(id);
        return R.ok("删除成功");
    }

    @Operation(summary = "批量删除商品")
    @DeleteMapping("/batch")
    public R<?> batchDelete(@RequestBody java.util.List<Long> ids) {
        ids.forEach(shangpinService::delete);
        return R.ok("批量删除成功");
    }

    // ========== 内部接口（Feign / Seata RM端调用） ==========

    @Operation(summary = "内部-商品详情")
    @GetMapping("/internal/productDetail")
    public R<?> internalProductDetail(@RequestParam Long id) {
        return R.ok(shangpinService.getById(id));
    }

    @Operation(summary = "内部-商品名称")
    @GetMapping("/internal/productName")
    public R<?> internalProductName(@RequestParam Long id) {
        ProductEntity entity = shangpinService.getById(id);
        return R.ok(entity != null ? entity.getProductName() : null);
    }

    @Operation(summary = "内部-扣减库存（Seata RM端）")
    @PostMapping("/internal/decreaseStock")
    public R<?> decreaseStock(@RequestParam Long id, @RequestParam Integer quantity) {
        return shangpinService.decreaseStock(id, quantity);
    }

    @Operation(summary = "内部-回补库存（取消订单回滚）")
    @PostMapping("/internal/increaseStock")
    public R<?> increaseStock(@RequestParam Long id, @RequestParam Integer quantity) {
        return shangpinService.increaseStock(id, quantity);
    }
}
