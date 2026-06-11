package com.cgb.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.product.entity.ShangpinEntity;
import com.cgb.product.service.ShangpinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品管理 Controller
 */
@Tag(name = "商品管理")
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
        return R.ok(result.getRecords());
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

    /** 内部接口 */
    @GetMapping("/internal/productDetail")
    public R<?> internalProductDetail(@RequestParam Long id) {
        return R.ok(shangpinService.getById(id));
    }

    @GetMapping("/internal/productName")
    public R<?> internalProductName(@RequestParam Long id) {
        return R.ok(shangpinService.getById(id).getMingcheng());
    }
}