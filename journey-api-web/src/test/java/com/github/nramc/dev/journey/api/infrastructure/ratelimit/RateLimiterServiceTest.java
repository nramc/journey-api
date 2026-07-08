package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimiterServiceTest {
    @Test
    void shouldAllowRequestsWithinCapacityAndBlockAfterLimit() {
        RateLimitProperties.Policy policy = new RateLimitProperties.Policy(2, Duration.ofMinutes(1));
        RateLimitProperties properties = new RateLimitProperties(Map.of("login", policy));

        RateLimiterService service = new RateLimiterService(properties);

        RateLimiterService.RateLimitDecision first = service.tryConsume("login", "client-1");
        RateLimiterService.RateLimitDecision second = service.tryConsume("login", "client-1");
        RateLimiterService.RateLimitDecision third = service.tryConsume("login", "client-1");

        assertThat(first.allowed()).isTrue();
        assertThat(second.allowed()).isTrue();
        assertThat(third.allowed()).isFalse();
        assertThat(third.retryAfterSeconds()).isGreaterThan(0);
    }

    @Test
    void shouldThrowWhenPolicyIsNotConfigured() {
        RateLimitProperties properties = new RateLimitProperties(Map.of());
        RateLimiterService service = new RateLimiterService(properties);

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.tryConsume("unknown-policy", "client-1"))
                .isInstanceOf(IllegalArgumentException.class);
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.assertPolicyConfigured("unknown-policy"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
