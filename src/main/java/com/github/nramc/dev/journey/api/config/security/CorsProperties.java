package com.github.nramc.dev.journey.api.config.security;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties("app.security.cors")
public record CorsProperties(@NotEmpty List<CorsProperty> properties) {

    public record CorsProperty(
            String path, List<String> allowedOrigins, List<String> allowedMethods
    ) {
    }
}
