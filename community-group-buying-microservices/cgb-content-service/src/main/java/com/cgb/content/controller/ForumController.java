package com.cgb.content.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.content.entity.ForumEntity;
import com.cgb.content.service.ForumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "论坛帖子")
@RestController
@RequestMapping("/forum")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;

    @Operation(summary = "发帖")
    @PostMapping
    public R<?> save(@RequestBody ForumEntity entity, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        entity.setUserid(userId);
        forumService.save(entity);
        return R.ok("发布成功");
    }

    @Operation(summary = "帖子列表")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) ForumEntity params) {
        IPage<ForumEntity> result = forumService.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "帖子详情")
    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(forumService.getById(id));
    }

    @Operation(summary = "修改帖子")
    @PutMapping
    public R<?> update(@RequestBody ForumEntity entity) {
        forumService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        forumService.delete(id);
        return R.ok("删除成功");
    }

    @Operation(summary = "点赞")
    @PostMapping("/thumbUp/{id}")
    public R<?> thumbUp(@PathVariable Long id) {
        forumService.thumbUp(id);
        return R.ok("点赞成功");
    }
}