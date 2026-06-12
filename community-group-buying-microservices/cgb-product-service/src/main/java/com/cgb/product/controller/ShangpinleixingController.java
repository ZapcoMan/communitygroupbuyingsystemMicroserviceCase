package com.cgb.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.product.entity.ShangpinleixingEntity;
import com.cgb.product.service.ShangpinleixingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品类型管理")
@RestController
@RequestMapping("/shangpinleixing")
@RequiredArgsConstructor
public class ShangpinleixingController {

    private final ShangpinleixingService service;

    @Operation(summary = "分页查询商品类型")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) ShangpinleixingEntity params,
                     @RequestParam(defaultValue = "1") Integer page,
                     @RequestParam(defaultValue = "10") Integer limit) {
        IPage<ShangpinleixingEntity> result = service.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "新增商品类型")
    @PostMapping
    public R<?> save(@RequestBody ShangpinleixingEntity entity) {
        service.save(entity);
        return R.ok("保存成功");
    }

    @Operation(summary = "修改商品类型")
    @PutMapping
    public R<?> update(@RequestBody ShangpinleixingEntity entity) {
        service.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "删除商品类型")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        service.delete(id);
        return R.ok("删除成功");
    }

    @Operation(summary = "批量删除商品类型")
    @DeleteMapping("/batch")
    public R<?> batchDelete(@RequestBody java.util.List<Long> ids) {
        ids.forEach(service::delete);
        return R.ok("批量删除成功");
    }
}
