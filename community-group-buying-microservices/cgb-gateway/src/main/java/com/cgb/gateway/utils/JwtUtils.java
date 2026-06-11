package com.cgb.gateway.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 */
@Slf4j
@Component
public class JwtUtils {

    private final SecretKey secretKey;
    private final long expirationSeconds;

    public JwtUtils(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expiration;
    }

    /**
     * 生成 Token
     */
    public String generateToken(Long userId, String role, String clientIP) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationSeconds * 1000);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .claim("clientIP", clientIP)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 验证 Token 签名
     */
    public boolean validateToken(String token, String clientIP) {
        try {
            Claims claims = parseToken(token);
            if (claims == null) return false;
            // 过期检查
            if (claims.getExpiration().before(new Date())) return false;
            // IP 绑定验证（如果存在）
            String tokenIP = claims.get("clientIP", String.class);
            if (tokenIP != null && !tokenIP.equals(clientIP)) {
                log.warn("IP 绑定不匹配: tokenIP={}, clientIP={}", tokenIP, clientIP);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.warn("Token 验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 解析 Token（不验证签名，用于路由转发时取值）
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * 获取 Token 中的用户 ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取 Token 中的角色
     */
    public String getRole(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.get("role", String.class);
    }
}