package com.cgb.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.product.entity.ShangpinEntity;
import com.cgb.product.service.ShangpinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 商品管理 Controller
 */
@Tag(name = "商品管理")
@Slf4j
@RestController
@RequestMapping("/shangpin")
@RequiredArgsConstructor
public class ShangpinController {

    private final ShangpinService shangpinService;

    @Operation(summary = "分页查询商品")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) ShangpinEntity params,
                     @RequestParam(defaultValue = "1") Integer page,
                     @RequestParam(defaultValue = "10") Integer limit) {
        IPage<ShangpinEntity> result = shangpinService.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(shangpinService.getById(id));
    }

    @Operation(summary = "新增商品")
    @PostMapping
    public R<?> save(@RequestBody ShangpinEntity entity) {
        shangpinService.save(entity);
        return R.ok("保存成功");
    }

    @Operation(summary = "修改商品")
    @PutMapping
    public R<?> update(@RequestBody ShangpinEntity entity) {
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

    /** 内部接口 - 获取商品详情 */
    @GetMapping("/internal/productDetail")
    public R<?> internalProductDetail(@RequestParam Long id) {
        return R.ok(shangpinService.getById(id));
    }

    /** 内部接口 - 获取商品名称 */
    @GetMapping("/internal/productName")
    public R<?> internalProductName(@RequestParam Long id) {
        return R.ok(shangpinService.getById(id).getMingcheng());
    }

    /** 内部接口 - 扣减库存（Seata RM 端，分布式事务分支） */
    @Operation(summary = "扣减库存（内部）")
    @PostMapping("/internal/decreaseStock")
    public R<?> internalDecreaseStock(@RequestParam Long id, @RequestParam Integer quantity) {
        try {
            shangpinService.decreaseStock(id, quantity);
            log.info("库存扣减成功: productId={}, quantity={}", id, quantity);
            return R.ok("库存扣减成功");
        } catch (Exception e) {
            log.error("库存扣减失败: productId={}, quantity={}", id, quantity, e);
            return R.fail(e.getMessage());
        }
    }

    /** 内部接口 - 回补库存（订单取消时回滚） */
    @Operation(summary = "回补库存（内部）")
    @PostMapping("/internal/increaseStock")
    public R<?> internalIncreaseStock(@RequestParam Long id, @RequestParam Integer quantity) {
        try {
            shangpinService.increaseStock(id, quantity);
            log.info("库存回补成功: productId={}, quantity={}", id, quantity);
            return R.ok("库存回补成功");
        } catch (Exception e) {
            log.error("库存回补失败: productId={}, quantity={}", id, quantity, e);
            return R.fail(e.getMessage());
        }
    }
}
