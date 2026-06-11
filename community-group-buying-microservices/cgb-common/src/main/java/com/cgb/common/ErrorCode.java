package com.cgb.common;

/**
 * 业务错误码枚举
 */
public enum ErrorCode {
    SUCCESS(0, "操作成功"),
    SYSTEM_ERROR(500, "系统异常，请稍后重试"),
    UNAUTHORIZED(401, "未授权，请重新登录"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    VALIDATION_ERROR(400, "参数校验失败"),
    RATE_LIMIT_ERROR(429, "请求过于频繁，请稍后重试"),
    USERNAME_PASSWORD_ERROR(401, "用户名或密码错误"),
    USER_DISABLED(401, "账号已被禁用"),
    TOKEN_EXPIRED(401, "登录已过期，请重新登录"),
    TOKEN_INVALID(401, "无效的登录凭证"),
    USER_NOT_FOUND(404, "用户不存在"),
    USER_ALREADY_EXISTS(409, "用户已存在"),
    PASSWORD_ERROR(400, "密码错误"),
    DATA_NOT_FOUND(404, "数据不存在"),
    DATA_CONFLICT(409, "数据已存在"),
    PERMISSION_DENIED(403, "权限不足");

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() { return code; }
    public String getMsg() { return msg; }
}