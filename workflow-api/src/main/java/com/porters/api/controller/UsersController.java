package com.porters.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.porters.api.service.UserXmlService;
import com.porters.api.service.XmlToJsonConverter;
import com.porters.cache.config.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/users")
public class UsersController {

    private final DomainCacheFacade domainCache;
    private final CacheKeyStrategy keys;
    private final CacheAsyncService async;
    private final UserXmlService xml;
    private final XmlToJsonConverter converter;

    public UsersController(DomainCacheFacade domainCache,
                           CacheKeyStrategy keys,
                           CacheAsyncService async,
                           UserXmlService xml,
                           XmlToJsonConverter converter) {
        this.domainCache = domainCache;
        this.keys = keys;
        this.async = async;
        this.xml = xml;
        this.converter = converter;
    }

    // sync cache (MISS -> load xml -> convert -> store)
    @GetMapping
    public JsonNode users() {
        return domainCache.getList("users", JsonNode.class, CacheConfiguration.DEFAULT_TTL,
                () -> converter.convert(xml.loadXml()));
    }

    // async warm-up (first call returns LOADING, next call should HIT)
    @GetMapping("/async")
    public Object usersAsync() {
        String key = keys.list("users");
        JsonNode result = async.getOrAsyncLoad(key, JsonNode.class, CacheConfiguration.DEFAULT_TTL,
                () -> converter.convert(xml.loadXml()));
        return result != null ? result : Map.of("status", "LOADING");
    }

    // rebuild cache NOW (force clean then load again)
    @PostMapping("/rebuild")
    public JsonNode rebuild() {
        domainCache.invalidateDomain("users");
        return users();
    }
}
