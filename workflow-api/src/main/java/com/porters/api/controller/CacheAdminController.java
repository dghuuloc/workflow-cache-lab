package com.porters.api.controller;

import com.porters.cache.config.CacheAdmin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/cache")
public class CacheAdminController {

    private final CacheAdmin cacheAdmin;

    public CacheAdminController(CacheAdmin cacheAdmin) {
        this.cacheAdmin = cacheAdmin;
    }

    // Clean ALL cache for current version (eg., v1:*)
    @DeleteMapping public long clearAll() {
        return cacheAdmin.invalidateAll();
    }

    // Clean a whole domain: v1:users*
    @DeleteMapping("/{domain}")
    public long clearDomain(@PathVariable String domain) {
        return cacheAdmin.invalidateDomain(domain);
    }

    // Clean one by id: v1:users:byId:123
    @DeleteMapping("/{domain}/{id}")
    public String clearById(@PathVariable String domain, @PathVariable String id) {
        cacheAdmin.invalidateOne(domain, id);
        return "OK";
    }
}
