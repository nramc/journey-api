package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

@ConfigurationProperties(prefix = "journey.module.infrastructure.rate-limit")
public record RateLimitProperties(Map<String, Policy> policies) {

    public RateLimitProperties {
        policies = policies == null ? Map.of() : Map.copyOf(policies);
    }

    public Policy policy(String policyName) {
        Policy policy = policies.get(policyName);
        if (policy == null) {
            throw new IllegalArgumentException("Rate limit policy '%s' is not configured".formatted(policyName));
        }
        return policy;
    }

    public record Policy(int capacity, Duration window) {
        public Policy {
            if (capacity <= 0) {
                throw new IllegalArgumentException("Rate limit capacity must be positive");
            }
            if (window == null || window.isNegative() || window.isZero()) {
                throw new IllegalArgumentException("Rate limit window must be positive");
            }
        }
    }
}
