package com.github.nramc.dev.journey.api.core.app;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record ApplicationProperties(
        @NotBlank String name,
        @NotBlank String version,
        @NotBlank @URL String uiAppUrl) {
}
