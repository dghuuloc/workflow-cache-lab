package com.porters.cache.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RedisCacheClient implements CacheClient {

    private final RedisTemplate<String, Object> redis;

    RedisCacheClient(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        Object value = redis.opsForValue().get(key);
        return value == null ? null : type.cast(value);
    }

    @Override
    public void set(String key, Object value, Duration ttl) {
        redis.opsForValue().set(
                key,
                value,
                ttl.toSeconds(),
                TimeUnit.SECONDS
        );
    }

    @Override
    public void delete(String key) {
        redis.delete(key);
    }

    @Override
    public long deleteByPrefix(String prefix) {
        Set<String> keys = redis.keys(prefix + "*");
        if (keys == null || keys.isEmpty()) return 0L;

        Long deleted = redis.delete(keys);
        return deleted == null ? 0L : deleted;
    }

    @Override
    public <T> T getOrLoad(String key,
                           Class<T> type,
                           Duration ttl,
                           Supplier<T> loader) {

        T cached = get(key, type);
        if (cached != null) {
            return cached;
        }

        T value = loader.get();
        set(key, value, ttl);
        return value;
    }
}
