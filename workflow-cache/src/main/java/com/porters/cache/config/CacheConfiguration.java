package com.porters.cache.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.porters.cache.metrics.CacheMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class CacheConfiguration {

    public static final String CACHE_VERSION = "v1";
    public static final Duration DEFAULT_TTL = Duration.ofMinutes(5);
    /**
     * Provide a RedisTemplate with JSON serialization.
     * If you already have another RedisTemplate bean, keep only one.
     */
    @Bean
    public RedisTemplate<String, JsonNode> redisTemplate(
            RedisConnectionFactory cf,
            ObjectMapper objectMapper
    ) {
        RedisTemplate<String, JsonNode> template = new RedisTemplate<>();
        template.setConnectionFactory(cf);

        // Key serializer
        StringRedisSerializer keySer = new StringRedisSerializer();
        template.setKeySerializer(keySer);
        template.setHashKeySerializer(keySer);

        // Value serializer (typed -> no @class needed)
        Jackson2JsonRedisSerializer<JsonNode> jsonSer =
                new Jackson2JsonRedisSerializer<>(objectMapper, JsonNode.class);

        template.setValueSerializer(jsonSer);
        template.setHashValueSerializer(jsonSer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Cache key generator: v1:users:list, v1:users:byId:123 ...
     */
    @Bean
    public CacheKeyStrategy cacheKeyStrategy() {
        return new CacheKeyStrategy() {

            @Override
            public String list(String domain) {
                return CACHE_VERSION + ":" + domain + ":list";
            }

            @Override
            public String byId(String domain, Object id) {
                return CACHE_VERSION + ":" + domain + ":byId:" + id;
            }

            @Override
            public String prefix(String domain) {
                return CACHE_VERSION + ":" + domain;
            }
        };
    }

    /**
     * Low-level cache client using Redis.
     */
    @Bean
    public CacheClient cacheClient(RedisTemplate<String, JsonNode> redisTemplate, CacheMetrics cacheMetrics) {
        return new RedisCacheClient(redisTemplate, cacheMetrics);
    }

    /**
     * Async cache refresh helper (simple async loader).
     */
    @Bean
    public CacheAsyncService cacheAsyncService(CacheClient cacheClient) {
        return new CacheAsyncService(cacheClient);
    }

    /**
     * Admin operations: invalidate one key, invalidate by domain prefix.
     */
    @Bean
    public CacheAdmin cacheAdmin(CacheClient cacheClient, CacheKeyStrategy keys) {
        return new CacheAdminImpl(cacheClient, keys);
    }

    /**
     * Generic facade for any domain (users/fields/options/etc.)
     * Usage:
     *   domainCache.getList("users", JsonNode.class, ttl, loader)
     */
    @Bean
    public DomainCacheFacade domainCacheFacade(CacheClient cacheClient, CacheKeyStrategy keys) {
        return new DomainCacheFacade(cacheClient, keys);
    }

}
