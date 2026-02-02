package com.porters.cache.config;

import java.time.Duration;
import java.util.function.Supplier;

public interface CacheClient {
    <T> T get(String key, Class<T> type);
    void set(String key, Object value, Duration ttl);
    void delete(String key);
    long deleteByPrefix(String prefix);
    <T> T getOrLoad(String key, Class<T> type, Duration ttl, Supplier<T> loader);
}