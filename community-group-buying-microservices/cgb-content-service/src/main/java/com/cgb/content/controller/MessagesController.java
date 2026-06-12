package com.cgb.content.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.content.entity.MessagesEntity;
import com.cgb.content.service.MessagesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import com.cgb.common.annotation.RateLimit;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "留言板")
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessagesController {

    private final MessagesService messagesService;

    @Operation(summary = "留言")
    @PostMapping
    @RateLimit(key = "message_post", count = 20, period = 1, unit = RateLimit.TimeUnit.MINUTES)
    public R<?> save(@RequestBody MessagesEntity entity, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        entity.setUserId(userId);
        messagesService.save(entity);
        return R.ok("留言成功");
    }

    @Operation(summary = "留言列表")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) MessagesEntity params) {
        IPage<MessagesEntity> result = messagesService.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "回复留言")
    @PostMapping("/reply/{id}")
    @RateLimit(key = "message_reply", count = 20, period = 1, unit = RateLimit.TimeUnit.MINUTES)
    public R<?> reply(@PathVariable Long id, @RequestParam String replyContent) {
        messagesService.reply(id, replyContent);
        return R.ok("回复成功");
    }

    @Operation(summary = "删除留言")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        messagesService.delete(id);
        return R.ok("删除成功");
    }
}