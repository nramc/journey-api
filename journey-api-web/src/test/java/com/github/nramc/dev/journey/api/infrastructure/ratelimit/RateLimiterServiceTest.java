package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimiterServiceTest {
    @Test
    void shouldAllowRequestsWithinCapacityAndBlockAfterLimit() {
        RateLimitProperties.Policy policy = new RateLimitProperties.Policy(
                "login", HttpMethod.POST, "/rest/login", 2, Duration.ofMinutes(1), RateLimitKey.CLIENT_IP
        );

        RateLimiterService service = new RateLimiterService();

        RateLimiterService.RateLimitDecision first = service.tryConsume(policy, "client-1");
        RateLimiterService.RateLimitDecision second = service.tryConsume(policy, "client-1");
        RateLimiterService.RateLimitDecision third = service.tryConsume(policy, "client-1");

        assertThat(first.allowed()).isTrue();
        assertThat(second.allowed()).isTrue();
        assertThat(third.allowed()).isFalse();
        assertThat(third.retryAfterSeconds()).isGreaterThan(0);
    }
}
