package com.github.nramc.dev.journey.api.security.totp.config;

import com.github.nramc.dev.journey.api.security.totp.TotpAlgorithm;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("app.security.totp")
@Builder(toBuilder = true)
@Validated
public record TotpProperties(
        @Positive @Min(6) @Max(16) int numberOfDigits,
        @Positive @Min(32) @Max(256) int secretLength,
        @NotNull TotpAlgorithm totpAlgorithm,
        @Positive @Min(30) long timeStepSizeInSeconds,
        @Positive @Min(0) @Max(5) int maxAllowedTimeStepDiscrepancy
) {
}
