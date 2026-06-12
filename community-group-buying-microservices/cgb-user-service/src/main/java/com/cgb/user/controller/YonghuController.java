package com.cgb.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.user.entity.MemberEntity;
import com.cgb.user.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.cgb.common.annotation.RateLimit;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/yonghu")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService yonghuService;

    @Operation(summary = "用户列表")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) MemberEntity params) {
        IPage<MemberEntity> result = yonghuService.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    @RateLimit(key = "user_register", count = 5, period = 1, unit = RateLimit.TimeUnit.MINUTES)
    public R<?> register(@RequestBody MemberEntity entity) {
        yonghuService.register(entity);
        return R.ok("注册成功");
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    @RateLimit(key = "user_login", count = 10, period = 1, unit = RateLimit.TimeUnit.MINUTES)
    public R<?> login(@RequestBody MemberEntity entity) {
        return yonghuService.login(entity.getAccount(), entity.getPassword());
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(yonghuService.getById(id));
    }

    @Operation(summary = "修改用户")
    @PutMapping
    public R<?> update(@RequestBody MemberEntity entity) {
        yonghuService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        yonghuService.delete(id);
        return R.ok("删除成功");
    }

    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    public R<?> batchDelete(@RequestBody java.util.List<Long> ids) {
        ids.forEach(yonghuService::delete);
        return R.ok("批量删除成功");
    }

    // ========== 内部接口 ==========

    @Operation(summary = "内部-用户信息")
    @GetMapping("/internal/userInfo")
    public R<?> internalUserInfo(@RequestParam Long userId) {
        return R.ok(yonghuService.getById(userId));
    }

    @Operation(summary = "内部-检查用户")
    @GetMapping("/internal/checkUser")
    public R<?> internalCheckUser(@RequestParam Long userId) {
        return R.ok(yonghuService.getById(userId) != null);
    }

    @Operation(summary = "内部-获取用户名")
    @GetMapping("/internal/getUsername")
    public R<?> internalGetUsername(@RequestParam Long userId) {
        MemberEntity entity = yonghuService.getById(userId);
        return R.ok(entity != null ? entity.getRealName() : null);
    }

    @Operation(summary = "内部-增加积分")
    @PostMapping("/internal/addPoints")
    public R<?> internalAddPoints(@RequestParam Long userId, @RequestParam Double points) {
        yonghuService.addPoints(userId, points);
        return R.ok();
    }
}
