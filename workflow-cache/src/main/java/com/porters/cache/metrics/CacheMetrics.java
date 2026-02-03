package com.porters.cache.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CacheMetrics {
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;

    public CacheMetrics(MeterRegistry registry) {
        this.cacheHitCounter = Counter.builder("workflow.cache.hit")
                .description("Cache hit count")
                .register(registry);
        this.cacheMissCounter = Counter.builder("workflow.cache.miss")
                .description("Cache miss count")
                .register(registry);
    }
    
    public void hit() {
        cacheHitCounter.increment();
    }
    
    public void miss() {
        cacheMissCounter.increment();
    }
    
}
