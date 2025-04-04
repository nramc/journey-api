package com.github.nramc.dev.journey.api.tests.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "environment")
public record EnvironmentProperties(
        @NotBlank String name,
        @NotBlank String baseUrl) {
}
