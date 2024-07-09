package com.github.nramc.dev.journey.api.security.totp.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.security.totp")
@Builder(toBuilder = true)
public record TotpProperties(
        @Positive @Min(6) @Max(16) int numberOfDigits,
        @Positive @Min(32) @Max(256) int secretLength
) {
}
