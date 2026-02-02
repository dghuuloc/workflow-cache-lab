package com.porters.cache.key;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
@Service
public class CacheVersionService {
    private static final String VERSION_KEY = "cache:version";

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheVersionService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public int getVersion() {
        Integer v = (Integer) redisTemplate.opsForValue().get(VERSION_KEY);
        return v == null ? 0 : v;
    }

    public void bumpVersion() {
        redisTemplate.opsForValue().increment(VERSION_KEY);
    }
}
