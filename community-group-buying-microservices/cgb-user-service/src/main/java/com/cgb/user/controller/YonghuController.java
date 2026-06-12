package com.cgb.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.common.annotation.IgnoreAuth;
import com.cgb.common.utils.CommonUtil;
import com.cgb.user.entity.YonghuEntity;
import com.cgb.user.service.YonghuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户（买家）Controller
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/yonghu")
@RequiredArgsConstructor
public class YonghuController {

    private final YonghuService yonghuService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    @IgnoreAuth
    public R<?> register(@RequestBody YonghuEntity entity) {
        return yonghuService.register(entity);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    @IgnoreAuth
    public R<?> login(@RequestBody YonghuEntity entity, HttpServletRequest request) {
        String clientIP = CommonUtil.getClientIP(request);
        return yonghuService.login(entity, clientIP);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public R<?> logout(HttpServletRequest request) {
        String token = request.getHeader("X-Token");
        return R.ok();
    }

    @Operation(summary = "分页查询用户")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) YonghuEntity params,
                     @RequestParam(defaultValue = "1") Integer page,
                     @RequestParam(defaultValue = "10") Integer limit) {
        IPage<YonghuEntity> result = yonghuService.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(yonghuService.getById(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public R<?> save(@RequestBody YonghuEntity entity) {
        yonghuService.save(entity);
        return R.ok("保存成功");
    }

    @Operation(summary = "修改用户")
    @PutMapping
    public R<?> update(@RequestBody YonghuEntity entity) {
        yonghuService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "修改密码")
    @PostMapping("/password")
    public R<?> changePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String token = request.getHeader("X-Token");
        String oldPassword = params.get("password");
        String newPassword = params.get("newpassword");
        if (oldPassword == null || newPassword == null) {
            return R.fail("参数不完整");
        }
        // 通过 token 获取用户ID，验证旧密码，更新新密码
        return yonghuService.changePassword(token, oldPassword, newPassword);
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

    /**
     * 内部接口（供其他微服务调用）
     */
    @Operation(summary = "获取用户信息（内部）")
    @GetMapping("/internal/userInfo")
    @IgnoreAuth
    public R<?> internalUserInfo(@RequestParam Long userId) {
        try {
            YonghuEntity user = yonghuService.getById(userId);
            user.setMima(null);
            return R.ok(user);
        } catch (Exception e) {
            return R.fail("用户不存在");
        }
    }

    @Operation(summary = "获取用户名（内部）")
    @GetMapping("/internal/getUsername")
    @IgnoreAuth
    public R<?> internalGetUsername(@RequestParam Long userId) {
        try {
            YonghuEntity user = yonghuService.getById(userId);
            return R.ok(user.getXingming());
        } catch (Exception e) {
            return R.fail("用户不存在");
        }
    }
}