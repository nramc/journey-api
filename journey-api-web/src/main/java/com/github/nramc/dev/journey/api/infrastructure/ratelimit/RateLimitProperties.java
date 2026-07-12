package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "journey.module.infrastructure.rate-limit")
@Validated
public record RateLimitProperties(List<@NotNull @Valid Policy> policies) {

    public RateLimitProperties {
        policies = policies == null ? List.of() : List.copyOf(policies);
    }

    public record Policy(@NotBlank String name,
                         @NotNull HttpMethod method,
                         @NotBlank String path,
                         @Positive int capacity,
                         @NotNull @DurationMin(seconds = 5) Duration window,
                         @NotNull RateLimitKey key) {
    }
}
