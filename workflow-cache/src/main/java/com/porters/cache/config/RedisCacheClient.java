package com.porters.cache.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.porters.cache.metrics.CacheMetrics;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RedisCacheClient implements CacheClient {
    private final RedisTemplate<String, JsonNode> redisTemplate;
    private final CacheMetrics cacheMetrics;

    RedisCacheClient(RedisTemplate<String, JsonNode> redisTemplate, CacheMetrics cacheMetrics) {
        this.redisTemplate = redisTemplate;
        this.cacheMetrics = cacheMetrics;
    }
    @Override
    public <T> T get(String key, Class<T> type) {
        JsonNode value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }

        // This CacheClient is specifically for JsonNode values
        if (!type.isAssignableFrom(JsonNode.class)) {
            throw new IllegalArgumentException(
                    "This cache stores JsonNode only. Requested type: " + type.getName()
            );
        }
        return type.cast(value);
    }

    @Override
    public void set(String key, Object value, Duration ttl) {
        if (!(value instanceof JsonNode)) {
            throw new IllegalArgumentException(
                    "This cache stores JsonNode only. Got: " + value.getClass().getName()
            );
        }
        redisTemplate.opsForValue().set(key, (JsonNode) value, ttl.toSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public long deleteByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys == null || keys.isEmpty()) return 0L;

        Long deleted = redisTemplate.delete(keys);
        return deleted == null ? 0L : deleted;
    }

    @Override
    public <T> T getOrLoad(String key, Class<T> type, Duration ttl, Supplier<T> loader) {
        T cached = get(key, type);
        if (cached != null) {
            cacheMetrics.hit();
            return cached;
        }

        cacheMetrics.miss();
        T value = loader.get();
        set(key, value, ttl);
        return value;
    }
}
