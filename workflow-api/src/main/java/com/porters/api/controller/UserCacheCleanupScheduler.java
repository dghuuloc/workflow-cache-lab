package com.porters.api.controller;

import com.porters.cache.config.CacheAdmin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class UserCacheCleanupScheduler {

    private final CacheAdmin admin;

    public UserCacheCleanupScheduler(@Qualifier("cacheAdmin") CacheAdmin admin) {
        this.admin = admin;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void nightlyClean() {
        admin.invalidateDomain("users");
    }

}
