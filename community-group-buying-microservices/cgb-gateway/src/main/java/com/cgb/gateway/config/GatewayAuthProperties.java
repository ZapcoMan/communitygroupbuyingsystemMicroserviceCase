package com.cgb.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关鉴权配置属性
 * 支持通过 Nacos 配置中心动态更新，无需重启网关
 *
 * <pre>
 * gateway:
 *   auth:
 *     white-list:
 *       - /user/users/login
 *       - /user/users/register
 *       - /actuator
 * </pre>
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "gateway.auth")
public class GatewayAuthProperties {

    /** 放行路径白名单（支持前缀匹配） */
    private List<String> whiteList = new ArrayList<>(List.of(
        "/user/users/login",
        "/user/users/register",
        "/user/yonghu/register",
        "/user/yonghu/login",
        "/user/users/forgot",
        "/doc.html",
        "/swagger-ui",
        "/v3/api-docs",
        "/actuator"
    ));
}
