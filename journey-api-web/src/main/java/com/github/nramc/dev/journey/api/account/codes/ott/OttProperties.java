package com.github.nramc.dev.journey.api.account.codes.ott;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Configuration properties for One-Time Token (OTT) based account recovery / login.
 */
@ConfigurationProperties("journey.module.account.security.ott")
@Builder(toBuilder = true)
@Validated
public record OttProperties(
        @NotNull Duration tokenValidity,
        @NotBlank String recoveryPath,
        @NotBlank String tokenQueryPram
) {
}
