package com.porters.cache.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfiguration {

    /**
     * Provide a RedisTemplate with JSON serialization.
     * If you already have another RedisTemplate bean, keep only one.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(cf);

        StringRedisSerializer keySer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valSer = new GenericJackson2JsonRedisSerializer();

        // Key serializers
        template.setKeySerializer(keySer);
        template.setHashKeySerializer(keySer);

        // Value serializers
        template.setValueSerializer(valSer);
        template.setHashValueSerializer(valSer);

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
                return CacheDefaults.CACHE_VERSION + ":" + domain + ":list";
            }

            @Override
            public String byId(String domain, Object id) {
                return CacheDefaults.CACHE_VERSION + ":" + domain + ":byId:" + id;
            }

            @Override
            public String prefix(String domain) {
                return CacheDefaults.CACHE_VERSION + ":" + domain;
            }
        };
    }

    /**
     * Low-level cache client using Redis.
     */
    @Bean
    public CacheClient cacheClient(RedisTemplate<String, Object> redisTemplate) {
        return new RedisCacheClient(redisTemplate);
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
