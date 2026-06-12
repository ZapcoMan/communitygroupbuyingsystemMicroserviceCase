package com.cgb.common.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * RateLimit 注解 AOP 切面实现
 * 基于 Redis + Lua 脚本实现滑动窗口限流
 */
@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    /** Lua 脚本：滑动窗口限流
     * KEYS[1] = 限流key
     * ARGV[1] = 窗口大小(秒)
     * ARGV[2] = 最大请求数
     * ARGV[3] = 当前时间戳(毫秒)
     */
    private static final String LUA_SCRIPT =
            "local key = KEYS[1] " +
            "local window = tonumber(ARGV[1]) " +
            "local limit = tonumber(ARGV[2]) " +
            "local now = tonumber(ARGV[3]) " +
            "local windowStart = now - window * 1000 " +
            // 移除窗口外的记录
            "redis.call('ZREMRANGEBYSCORE', key, 0, windowStart) " +
            // 统计当前窗口请求数
            "local count = redis.call('ZCARD', key) " +
            "if count < limit then " +
            "  redis.call('ZADD', key, now, now .. ':' .. math.random(1, 1000000)) " +
            "  redis.call('EXPIRE', key, window) " +
            "  return 1 " +
            "else " +
            "  return 0 " +
            "end";

    public RateLimitAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(com.cgb.common.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        String key = "rate_limit:" + rateLimit.key() + ":" + method.getDeclaringClass().getSimpleName() + "." + method.getName();
        int periodSeconds = rateLimit.period() * rateLimit.unit().getSeconds();
        int maxCount = rateLimit.count();

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);
        Long result = redisTemplate.execute(script,
                Collections.singletonList(key),
                String.valueOf(periodSeconds),
                String.valueOf(maxCount),
                String.valueOf(System.currentTimeMillis()));

        if (result == null || result == 0L) {
            log.warn("接口限流触发: key={}, period={}s, limit={}", key, periodSeconds, maxCount);
            throw new com.cgb.common.EIException("操作过于频繁，请稍后再试");
        }

        return joinPoint.proceed();
    }
}
