package com.cgb.groupbuy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.groupbuy.entity.TuanCommentEntity;
import com.cgb.groupbuy.service.TuanCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "团购评论管理")
@RestController
@RequestMapping("/tuancomment")
@RequiredArgsConstructor
public class TuanCommentController {

    private final TuanCommentService service;

    @Operation(summary = "分页查询团购评论")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) TuanCommentEntity params,
                     @RequestParam(defaultValue = "1") Integer page,
                     @RequestParam(defaultValue = "10") Integer limit) {
        IPage<TuanCommentEntity> result = service.queryPage(params);
        return R.ok(result.getRecords());
    }

    @Operation(summary = "发表团购评论")
    @PostMapping
    public R<?> save(@RequestBody TuanCommentEntity entity) {
        service.save(entity);
        return R.ok("评论成功");
    }

    @Operation(summary = "更新评论/回复")
    @PutMapping
    public R<?> update(@RequestBody TuanCommentEntity entity) {
        service.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        service.delete(id);
        return R.ok("删除成功");
    }

    @Operation(summary = "批量删除评论")
    @DeleteMapping("/batch")
    public R<?> batchDelete(@RequestBody java.util.List<Long> ids) {
        ids.forEach(service::delete);
        return R.ok("批量删除成功");
    }
}
