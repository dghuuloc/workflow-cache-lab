package com.porters.cache.config;

public interface CacheAdmin {
    void invalidateOne(String domain, Object id);
    long invalidateDomain(String domain);
}
