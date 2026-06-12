package com.cgb.common.auth;

/**
 * 微服务内部调用认证常量
 * 内部接口 (/internal/**) 通过 Header 中的 Service Token 验证调用方身份
 */
public final class InternalAuthConstants {

    private InternalAuthConstants() {}

    /** 内部调用 Token Header 名称 */
    public static final String HEADER_NAME = "X-Internal-Token";

    /** 内部调用 Token 值（生产环境通过环境变量注入） */
    public static final String TOKEN = System.getenv().getOrDefault("INTERNAL_SERVICE_TOKEN",
            "cgb-internal-service-token-change-in-production");

    /** 验证 Token 是否合法 */
    public static boolean isValid(String token) {
        return TOKEN.equals(token);
    }
}
