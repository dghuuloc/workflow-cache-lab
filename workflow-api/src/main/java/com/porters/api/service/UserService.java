package com.porters.api.service;

import com.porters.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // simulate DB
    public User loadFromDB(Long id) {
        System.out.println("Load from DB for user id: " + id);
        return new User(id, "Tom", 20);
    }

    // VERSIONED CACHE
    @Cacheable(value = "users", key = "#id")
    public User getUser(Long id) {
        return loadFromDB(id);
    }

    // INVALIDATE SINGLE USER CACHE
    @CacheEvict(value = "users", key = "#id")
    public void invalidateUser(Long id) {
        System.out.println("Cache invalidated for user " + id);
    }

    public void saveUserHash(User user) {
        String key = "user:hash";
        redisTemplate.opsForHash().put(key, user.getId().toString(), user);
    }

    public User getUserHash(Long id) {
        return (User) redisTemplate.opsForHash().get("user:hash", id.toString());
    }

    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        System.out.println("Cache invalidated for user " + id);
    }

    // CLEAN ALL USER CACHE
    @CacheEvict(value = "users", allEntries = true)
    public void clearAllUsersCache() {
        System.out.println("All user cache cleared");
    }

}
