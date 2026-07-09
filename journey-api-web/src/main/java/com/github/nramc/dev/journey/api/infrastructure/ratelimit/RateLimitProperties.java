package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.time.DurationMin;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.Map;

@ConfigurationProperties(prefix = "journey.module.infrastructure.rate-limit")
@Validated
public record RateLimitProperties(Map<@NotBlank String, @NotNull @Valid Policy> policies) {

    public RateLimitProperties {
        policies = policies == null ? Map.of() : Map.copyOf(policies);
    }

    @NonNull
    public Policy policy(@NonNull String policyName) {
        return policies.get(policyName);
    }

    public record Policy(@Positive int capacity, @NotNull @DurationMin(seconds = 5) Duration window) {
    }
}
