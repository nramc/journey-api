package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.config;

import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpAlgorithm;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
        @Positive @Min(0) int maxAllowedTimeStepDiscrepancy,
        @NotBlank String qrType,
        @NotBlank String qrIssuer,
        @Positive @Min(50) int qrWidth,
        @Positive @Min(50) int qrHeight
) {
}
