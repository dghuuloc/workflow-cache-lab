package com.porters.cache.config;

public interface CacheAdmin {
    void invalidateOne(String domain, Object id);   // clean by id
    long invalidateDomain(String domain);   // clean domain prefix
    long invalidateAll();   // clean all (for current version)
}
