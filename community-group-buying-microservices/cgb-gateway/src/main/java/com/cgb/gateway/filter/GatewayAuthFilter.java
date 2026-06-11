package com.cgb.gateway.filter;

import com.cgb.gateway.service.RedisTokenService;
import com.cgb.gateway.utils.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 网关全局鉴权过滤器
 * 优先级最高，在所有路由之前执行
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtils jwtUtils;
    private final RedisTokenService redisTokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 放行的路径（无需登录） */
    private static final List<String> WHITE_LIST = List.of(
        "/user/users/login",
        "/user/users/register",
        "/user/yonghu/register",
        "/user/yonghu/login",
        "/user/users/forgot",
        "/doc.html",
        "/swagger-ui",
        "/v3/api-docs",
        "/actuator"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod().name())) {
            return chain.filter(exchange);
        }

        // 放行白名单路径
        for (String whitePath : WHITE_LIST) {
            if (path.startsWith(whitePath)) {
                return chain.filter(exchange);
            }
        }

        // 获取客户端 IP
        String clientIP = getClientIP(request);
        String token = extractToken(request);

        if (token == null) {
            log.warn("路径[{}]无 Token，拒绝访问", path);
            return unauthorized(exchange, "请先登录");
        }

        // 验证 JWT 签名
        if (!jwtUtils.validateToken(token, clientIP)) {
            log.warn("Token 验证失败，路径: {}", path);
            return unauthorized(exchange, "登录已过期，请重新登录");
        }

        // 验证 Redis Token 会话是否存在
        if (!redisTokenService.existsToken(token)) {
            log.warn("Token 不在 Redis 中，路径: {}", path);
            return unauthorized(exchange, "登录已过期，请重新登录");
        }

        // 解析用户信息，附加到请求头传递给下游微服务
        Long userId = jwtUtils.getUserId(token);
        String role = jwtUtils.getRole(token);

        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-User-Role", role != null ? role : "")
                .header("X-Client-IP", clientIP)
                .header("X-Token", token)
                .build();

        // 刷新 Token 有效期
        redisTokenService.refreshToken(token);

        log.debug("鉴权通过，userId={}, path={}", userId, path);
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // 最高优先级
    }

    /**
     * 从请求头提取 Token
     */
    private String extractToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst("Token");
        if (token != null && !token.isEmpty()) return token;
        // 也支持 Authorization: Bearer xxx
        String auth = request.getHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return null;
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIP(ServerHttpRequest request) {
        List<String> headers = List.of(
            "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
        );
        for (String header : headers) {
            String ip = request.getHeaders().getFirst(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : "127.0.0.1";
    }

    /**
     * 返回 401 未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            Map<String, Object> body = Map.of(
                "code", 401,
                "msg", message,
                "data", Map.of()
            );
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (JsonProcessingException e) {
            byte[] bytes = "{\"code\":401,\"msg\":\"" + message + "\"}".getBytes();
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        }
    }
}