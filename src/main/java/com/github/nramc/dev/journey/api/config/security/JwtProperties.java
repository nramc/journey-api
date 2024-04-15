package com.github.nramc.dev.journey.api.config.security;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Validated
@ConfigurationProperties("jwt")
public record JwtProperties(
        @Resource RSAPublicKey publicKey,
        @Resource RSAPrivateKey privateKey,
        @NotBlank String issuer,
        @Positive long ttlInSeconds
) {
}
