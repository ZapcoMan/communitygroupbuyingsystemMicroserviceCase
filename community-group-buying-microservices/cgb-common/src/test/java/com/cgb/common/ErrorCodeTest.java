package com.cgb.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 业务错误码枚举 ErrorCode 的单元测试
 */
@DisplayName("ErrorCode - 业务错误码枚举")
class ErrorCodeTest {

    @Test
    @DisplayName("SUCCESS 错误码为 0")
    void success() {
        assertEquals(0, ErrorCode.SUCCESS.getCode());
        assertEquals("操作成功", ErrorCode.SUCCESS.getMsg());
    }

    @Test
    @DisplayName("SYSTEM_ERROR 错误码为 500")
    void systemError() {
        assertEquals(500, ErrorCode.SYSTEM_ERROR.getCode());
        assertEquals("系统异常，请稍后重试", ErrorCode.SYSTEM_ERROR.getMsg());
    }

    @Test
    @DisplayName("UNAUTHORIZED 错误码为 401")
    void unauthorized() {
        assertEquals(401, ErrorCode.UNAUTHORIZED.getCode());
        assertEquals("未授权，请重新登录", ErrorCode.UNAUTHORIZED.getMsg());
    }

    @Test
    @DisplayName("FORBIDDEN 错误码为 403")
    void forbidden() {
        assertEquals(403, ErrorCode.FORBIDDEN.getCode());
        assertEquals("禁止访问", ErrorCode.FORBIDDEN.getMsg());
    }

    @Test
    @DisplayName("NOT_FOUND 错误码为 404")
    void notFound() {
        assertEquals(404, ErrorCode.NOT_FOUND.getCode());
        assertEquals("资源不存在", ErrorCode.NOT_FOUND.getMsg());
    }

    @Test
    @DisplayName("VALIDATION_ERROR 错误码为 400")
    void validationError() {
        assertEquals(400, ErrorCode.VALIDATION_ERROR.getCode());
        assertEquals("参数校验失败", ErrorCode.VALIDATION_ERROR.getMsg());
    }

    @Test
    @DisplayName("RATE_LIMIT_ERROR 错误码为 429")
    void rateLimitError() {
        assertEquals(429, ErrorCode.RATE_LIMIT_ERROR.getCode());
        assertEquals("请求过于频繁，请稍后重试", ErrorCode.RATE_LIMIT_ERROR.getMsg());
    }

    @Test
    @DisplayName("用户相关错误码验证")
    void userErrorCodes() {
        assertEquals(401, ErrorCode.USERNAME_PASSWORD_ERROR.getCode());
        assertEquals(401, ErrorCode.USER_DISABLED.getCode());
        assertEquals(401, ErrorCode.TOKEN_EXPIRED.getCode());
        assertEquals(401, ErrorCode.TOKEN_INVALID.getCode());
        assertEquals(404, ErrorCode.USER_NOT_FOUND.getCode());
        assertEquals(409, ErrorCode.USER_ALREADY_EXISTS.getCode());
        assertEquals(400, ErrorCode.PASSWORD_ERROR.getCode());
    }

    @Test
    @DisplayName("数据相关错误码验证")
    void dataErrorCodes() {
        assertEquals(404, ErrorCode.DATA_NOT_FOUND.getCode());
        assertEquals(409, ErrorCode.DATA_CONFLICT.getCode());
        assertEquals(403, ErrorCode.PERMISSION_DENIED.getCode());
    }

    @Test
    @DisplayName("valueOf() - 枚举名称解析")
    void valueOf() {
        assertEquals(ErrorCode.SUCCESS, ErrorCode.valueOf("SUCCESS"));
        assertEquals(ErrorCode.SYSTEM_ERROR, ErrorCode.valueOf("SYSTEM_ERROR"));
    }

    @Test
    @DisplayName("values() - 包含所有枚举值")
    void values() {
        ErrorCode[] values = ErrorCode.values();
        assertTrue(values.length >= 15, "应至少有 15 个错误码枚举值");
    }
}
