package com.github.nramc.dev.journey.api.infrastructure.actuator;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Builder(toBuilder = true)
public record ApplicationProperties(
        @NotBlank String name,
        @NotBlank String version,
        @NotBlank @URL String uiAppUrl) {
}
