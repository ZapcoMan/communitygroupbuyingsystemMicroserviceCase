package com.cgb.common.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 多级缓存管理器（L1: Caffeine 本地缓存 + L2: Redis 分布式缓存）
 * <p>
 * 读取流程：L1 → L2 → DB（supplier），回写上级缓存
 * 写入流程：同时写入 L1 + L2
 * 删除流程：同时删除 L1 + L2
 */
@Slf4j
public class MultiLevelCache {

    private final Cache<String, Object> localCache;
    private final RedisTemplate<String, Object> redisTemplate;
    private final long redisTtlSeconds;

    public MultiLevelCache(RedisTemplate<String, Object> redisTemplate,
                           long localMaxSize,
                           long localExpireMinutes,
                           long redisTtlSeconds) {
        this.redisTemplate = redisTemplate;
        this.redisTtlSeconds = redisTtlSeconds;
        this.localCache = Caffeine.newBuilder()
                .maximumSize(localMaxSize)
                .expireAfterWrite(localExpireMinutes, TimeUnit.MINUTES)
                .build();
    }

    /**
     * 获取缓存，依次从 L1 → L2 → supplier 加载
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type, Supplier<T> supplier) {
        // L1: 本地缓存
        Object value = localCache.getIfPresent(key);
        if (value != null) {
            log.debug("L1 cache hit: {}", key);
            return (T) value;
        }

        // L2: Redis 缓存
        value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            log.debug("L2 cache hit: {}", key);
            localCache.put(key, value);  // 回填 L1
            return (T) value;
        }

        // 从数据源加载
        if (supplier != null) {
            T data = supplier.get();
            if (data != null) {
                put(key, data);
            }
            return data;
        }
        return null;
    }

    /**
     * 写入缓存（同时写 L1 + L2）
     */
    public void put(String key, Object value) {
        localCache.put(key, value);
        redisTemplate.opsForValue().set(key, value, redisTtlSeconds, TimeUnit.SECONDS);
    }

    /**
     * 删除缓存（同时删 L1 + L2）
     */
    public void evict(String key) {
        localCache.invalidate(key);
        redisTemplate.delete(key);
        log.debug("Cache evicted: {}", key);
    }

    /**
     * 清空本地缓存
     */
    public void evictLocalAll() {
        localCache.invalidateAll();
    }
}
