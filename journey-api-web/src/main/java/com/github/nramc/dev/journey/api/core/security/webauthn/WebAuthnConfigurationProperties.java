package com.github.nramc.dev.journey.api.core.security.webauthn;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.webauthn")
public record WebAuthnConfigurationProperties(
        String rpId,
        String rpName,
        String origin
) {
}
