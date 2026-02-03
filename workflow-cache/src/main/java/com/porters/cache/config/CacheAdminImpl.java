package com.porters.cache.config;

import org.springframework.stereotype.Component;

@Component
public class CacheAdminImpl implements CacheAdmin {

    private final CacheClient cache;
    private final  CacheKeyStrategy keys;

    CacheAdminImpl(CacheClient cache, CacheKeyStrategy keys) {
        this.cache = cache;
        this.keys = keys;
    }

    @Override
    public void invalidateOne(String domain, Object id) {
        cache.delete(keys.byId(domain, id));
    }

    @Override
    public long invalidateDomain(String domain) {
        return cache.deleteByPrefix(keys.prefix(domain));
    }

    @Override public long invalidateAll() {
        // delete everything for this version: v1:*
        return cache.deleteByPrefix(CacheDefaults.CACHE_VERSION + ":");
    }
}
