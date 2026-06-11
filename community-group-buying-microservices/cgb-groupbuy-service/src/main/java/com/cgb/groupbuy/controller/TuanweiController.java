package com.cgb.groupbuy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.groupbuy.entity.TuanweiEntity;
import com.cgb.groupbuy.service.TuanweiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "团长管理")
@RestController
@RequestMapping("/tuanwei")
@RequiredArgsConstructor
public class TuanweiController {

    private final TuanweiService tuanweiService;

    @Operation(summary = "发起团购")
    @PostMapping
    public R<?> save(@RequestBody TuanweiEntity entity, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        entity.setUserid(userId);
        tuanweiService.save(entity);
        return R.ok("发起成功");
    }

    @Operation(summary = "分页查询团购")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) TuanweiEntity params) {
        IPage<TuanweiEntity> result = tuanweiService.queryPage(params);
        return R.ok(result.getRecords());
    }

    @Operation(summary = "团购详情")
    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(tuanweiService.getById(id));
    }

    @Operation(summary = "修改团购")
    @PutMapping
    public R<?> update(@RequestBody TuanweiEntity entity) {
        tuanweiService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "删除团购")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        tuanweiService.delete(id);
        return R.ok("删除成功");
    }
}