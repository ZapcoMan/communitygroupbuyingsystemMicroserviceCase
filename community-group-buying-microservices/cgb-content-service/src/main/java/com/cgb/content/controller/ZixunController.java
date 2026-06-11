package com.cgb.content.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.content.entity.ZixunEntity;
import com.cgb.content.service.ZixunService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "团购资讯")
@RestController
@RequestMapping("/zixun")
@RequiredArgsConstructor
public class ZixunController {

    private final ZixunService zixunService;

    @Operation(summary = "新增资讯")
    @PostMapping
    public R<?> save(@RequestBody ZixunEntity entity) {
        zixunService.save(entity);
        return R.ok("发布成功");
    }

    @Operation(summary = "资讯列表")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) ZixunEntity params) {
        IPage<ZixunEntity> result = zixunService.queryPage(params);
        return R.ok(result.getRecords());
    }

    @Operation(summary = "资讯详情")
    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(zixunService.getById(id));
    }

    @Operation(summary = "修改资讯")
    @PutMapping
    public R<?> update(@RequestBody ZixunEntity entity) {
        zixunService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "删除资讯")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        zixunService.delete(id);
        return R.ok("删除成功");
    }
}