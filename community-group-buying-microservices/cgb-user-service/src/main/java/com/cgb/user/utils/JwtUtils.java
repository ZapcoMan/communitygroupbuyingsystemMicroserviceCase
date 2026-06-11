package com.cgb.user.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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

    public boolean validateToken(String token, String clientIP) {
        try {
            Claims claims = parseToken(token);
            if (claims == null) return false;
            if (claims.getExpiration().before(new Date())) return false;
            String tokenIP = claims.get("clientIP", String.class);
            if (tokenIP != null && !tokenIP.equals(clientIP)) return false;
            return true;
        } catch (Exception e) {
            log.warn("Token 验证失败: {}", e.getMessage());
            return false;
        }
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            return null;
        }
    }

    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String getRole(String token) {
        Claims claims = parseToken(token);
        return claims == null ? null : claims.get("role", String.class);
    }
}