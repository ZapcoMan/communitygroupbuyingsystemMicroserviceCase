package com.cgb.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis Token 会话管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final StringRedisTemplate redisTemplate;

    private static final String TOKEN_PREFIX = "cgb:token:";
    private static final long DEFAULT_EXPIRE_SECONDS = 3600L;

    /**
     * 保存 Token 会话
     */
    public void saveToken(String token, String userId, String role, String tableName) {
        String key = TOKEN_PREFIX + token;
        String value = userId + ":" + role + ":" + tableName;
        redisTemplate.opsForValue().set(key, value, DEFAULT_EXPIRE_SECONDS, TimeUnit.SECONDS);
        log.debug("保存 Token 会话: {}", key);
    }

    /**
     * 刷新 Token 有效期
     */
    public void refreshToken(String token) {
        String key = TOKEN_PREFIX + token;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.expire(key, DEFAULT_EXPIRE_SECONDS, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取 Token 会话
     */
    public String getTokenSession(String token) {
        String key = TOKEN_PREFIX + token;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 检查 Token 是否存在
     */
    public boolean existsToken(String token) {
        String key = TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 删除 Token（登出）
     */
    public void deleteToken(String token) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.delete(key);
        log.debug("删除 Token 会话: {}", key);
    }
}