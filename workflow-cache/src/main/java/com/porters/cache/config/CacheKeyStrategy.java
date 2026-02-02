package com.porters.cache.config;

public interface CacheKeyStrategy {
    String list(String domain);
    String byId(String domain, Object id);
    String prefix(String domain);

}
