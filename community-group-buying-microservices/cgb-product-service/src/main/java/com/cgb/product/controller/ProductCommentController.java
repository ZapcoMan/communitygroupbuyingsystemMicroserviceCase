package com.cgb.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.product.entity.ProductCommentEntity;
import com.cgb.product.service.ProductCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品评价")
@RestController
@RequestMapping("/shangpin/comment")
@RequiredArgsConstructor
public class ProductCommentController {

    private final ProductCommentService commentService;

    @Operation(summary = "发表评论")
    @PostMapping
    public R<?> save(@RequestBody ProductCommentEntity entity, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        entity.setUserId(userId);
        commentService.save(entity);
        return R.ok("评论成功");
    }

    @Operation(summary = "分页查询评论")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) ProductCommentEntity params,
                     @RequestParam(defaultValue = "1") Integer page,
                     @RequestParam(defaultValue = "10") Integer limit) {
        IPage<ProductCommentEntity> result = commentService.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        commentService.delete(id);
        return R.ok("删除成功");
    }

    @Operation(summary = "更新评论/回复")
    @PutMapping
    public R<?> update(@RequestBody ProductCommentEntity entity) {
        commentService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "批量删除评论")
    @DeleteMapping("/batch")
    public R<?> batchDelete(@RequestBody java.util.List<Long> ids) {
        ids.forEach(commentService::delete);
        return R.ok("批量删除成功");
    }
}