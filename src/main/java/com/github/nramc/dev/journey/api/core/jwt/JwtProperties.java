package com.github.nramc.dev.journey.api.core.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

@Validated
@ConfigurationProperties("jwt")
public record JwtProperties(
        @NotNull RSAPublicKey publicKey,
        @NotNull RSAPrivateKey privateKey,
        @NotBlank String issuer,
        @NotNull Duration ttl
) {
}
