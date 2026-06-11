package com.cgb.user.service;

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
    private static final long DEFAULT_EXPIRE = 3600L;

    public void saveToken(String token, String userId, String role, String tableName) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, userId + ":" + role + ":" + tableName,
                DEFAULT_EXPIRE, TimeUnit.SECONDS);
        log.debug("保存 Token: {}", key);
    }

    public void refreshToken(String token) {
        String key = TOKEN_PREFIX + token;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.expire(key, DEFAULT_EXPIRE, TimeUnit.SECONDS);
        }
    }

    public String getTokenSession(String token) {
        return redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
    }

    public boolean existsToken(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_PREFIX + token));
    }

    public void deleteToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
        log.debug("删除 Token: {}", TOKEN_PREFIX + token);
    }
}