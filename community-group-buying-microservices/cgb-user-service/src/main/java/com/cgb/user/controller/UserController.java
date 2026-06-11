package com.cgb.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.common.annotation.IgnoreAuth;
import com.cgb.common.utils.CommonUtil;
import com.cgb.user.entity.UserEntity;
import com.cgb.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员 Controller
 */
@Tag(name = "管理员管理")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    @IgnoreAuth
    public R<?> login(@RequestBody UserEntity entity, HttpServletRequest request) {
        String clientIP = CommonUtil.getClientIP(request);
        return userService.login(entity, clientIP);
    }

    @Operation(summary = "管理员登出")
    @PostMapping("/logout")
    public R<?> logout(HttpServletRequest request) {
        return R.ok();
    }

    @Operation(summary = "分页查询管理员")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) UserEntity params) {
        IPage<UserEntity> result = userService.queryPage(params);
        return R.ok(result.getRecords());
    }

    @Operation(summary = "管理员详情")
    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(userService.getById(id));
    }

    @Operation(summary = "新增管理员")
    @PostMapping
    public R<?> save(@RequestBody UserEntity entity) {
        userService.save(entity);
        return R.ok("保存成功");
    }

    @Operation(summary = "修改管理员")
    @PutMapping
    public R<?> update(@RequestBody UserEntity entity) {
        userService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "删除管理员")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        userService.delete(id);
        return R.ok("删除成功");
    }
}