package com.github.nramc.dev.journey.api.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties("app.user")
public record ApplicationUserProperties(
        String username,
        String password,
        Set<String> roles,
        String name) {
}
