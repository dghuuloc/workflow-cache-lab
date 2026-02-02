package com.porters.cache.config;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class CacheAsyncService {

    private final CacheClient cache;
    private final Executor executor = Executors.newFixedThreadPool(4);

    public CacheAsyncService(CacheClient cache) {
        this.cache = cache;
    }

    public <T> T getOrAsyncLoad(String key, Class<T> type, Duration ttl, Supplier<T> loader) {
        T cached = cache.get(key, type);
        if (cached != null) {
            return cached;
        }

        CompletableFuture.runAsync(() -> {
            T value = loader.get();
            cache.set(key, value, ttl);
        }, executor);

        return null;
    }
}
