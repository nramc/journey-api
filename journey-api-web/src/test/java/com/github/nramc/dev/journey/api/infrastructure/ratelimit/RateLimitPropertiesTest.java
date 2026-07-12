package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitPropertiesTest {

    @Test
    void shouldDefaultToEmptyListWhenPoliciesIsNull() {
        RateLimitProperties properties = new RateLimitProperties(null);

        assertThat(properties.policies()).isEmpty();
    }

    @Test
    void shouldPreserveProvidedPolicies() {
        RateLimitProperties.Policy policy = new RateLimitProperties.Policy(
                "login", HttpMethod.POST, "/rest/login", 5, Duration.ofMinutes(1), RateLimitKey.CLIENT_IP
        );

        RateLimitProperties properties = new RateLimitProperties(List.of(policy));

        assertThat(properties.policies()).containsExactly(policy);
    }
}
