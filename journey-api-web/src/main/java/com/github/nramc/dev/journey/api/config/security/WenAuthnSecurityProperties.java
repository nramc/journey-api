package com.github.nramc.dev.journey.api.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "app.security.webauthn")
public record WenAuthnSecurityProperties(
        boolean enabled,
        String rpId,
        String rpName,
        String rpIcon,
        Set<String> allowedOrigins) {
}
