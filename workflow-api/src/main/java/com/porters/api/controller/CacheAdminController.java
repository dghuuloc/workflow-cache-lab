package com.porters.api.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.porters.cache.config.CacheAdmin;

@RestController
@RequestMapping("/admin/cache")
public class CacheAdminController {

    private final CacheAdmin admin;

    public CacheAdminController(@Qualifier("cacheAdmin") CacheAdmin admin) {
        this.admin = admin;
    }

    // clear all keys under a domain prefix: v1:users*
    @DeleteMapping("/{domain}")
    public long clearDomain(@PathVariable String domain) {
        return admin.invalidateDomain(domain);
    }

    // clear one by id: v1:users:byId:123
    @DeleteMapping("/{domain}/{id}")
    public String clearOne(@PathVariable String domain, @PathVariable String id) {
        admin.invalidateOne(domain, id);
        return "OK";
    }

}
