package com.cgb.content.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.content.entity.ConfigEntity;
import com.cgb.content.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "系统配置")
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @Operation(summary = "分页查询配置")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) ConfigEntity params,
                     @RequestParam(defaultValue = "1") Integer page,
                     @RequestParam(defaultValue = "10") Integer limit) {
        IPage<ConfigEntity> result = configService.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "新增配置")
    @PostMapping
    public R<?> save(@RequestBody ConfigEntity entity) {
        configService.save(entity);
        return R.ok("保存成功");
    }

    @Operation(summary = "修改配置")
    @PutMapping
    public R<?> update(@RequestBody ConfigEntity entity) {
        configService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        configService.delete(id);
        return R.ok("删除成功");
    }

    @Operation(summary = "批量删除配置")
    @DeleteMapping("/batch")
    public R<?> batchDelete(@RequestBody java.util.List<Long> ids) {
        ids.forEach(configService::delete);
        return R.ok("批量删除成功");
    }
}
