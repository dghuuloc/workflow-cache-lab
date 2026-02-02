package com.porters.cache.config;

import java.time.Duration;
import java.util.function.Supplier;

public class DomainCacheFacade {

    private final CacheClient cacheClient;
    private final CacheKeyStrategy cacheKeyStrategy;

    public DomainCacheFacade(CacheClient cacheClient, CacheKeyStrategy cacheKeyStrategy) {
        this.cacheClient = cacheClient;
        this.cacheKeyStrategy = cacheKeyStrategy;
    }

    public <T> T getList(String domain, Class<T> type, Duration ttl, Supplier<T> loader) {
        return cacheClient.getOrLoad(cacheKeyStrategy.list(domain), type, ttl, loader);
    }

    public void invalidateOne(String domain, Object id) {
        cacheClient.delete(cacheKeyStrategy.byId(domain, id));
    }

    public long invalidateDomain(String domain) {
        return cacheClient.deleteByPrefix(cacheKeyStrategy.prefix(domain));
    }
}
