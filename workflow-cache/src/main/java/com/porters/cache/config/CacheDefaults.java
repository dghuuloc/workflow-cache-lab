package com.porters.cache.config;

import java.time.Duration;

public class CacheDefaults {
    public static final String CACHE_VERSION = "v1";
    public static final Duration DEFAULT_TTL = Duration.ofMinutes(5);
}
