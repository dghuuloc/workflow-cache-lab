package com.porters.api.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class UserXmlService {

    private final ResourceLoader loader;

    public UserXmlService(ResourceLoader loader) {
        this.loader = loader;
    }

    public String loadXml() {
        try {
            Resource r = loader.getResource("classpath:data/users.xml");
            return new String(r.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

