package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import java.time.Duration;

public class RateLimiterService {
    private final RateLimitProperties properties;
    private final Cache<String, Bucket> buckets;

    public RateLimiterService(RateLimitProperties properties) {
        this.properties = properties;
        this.buckets = Caffeine.newBuilder()
                .maximumSize(100_000)
                .expireAfterAccess(Duration.ofHours(1))
                .build();
    }

    public RateLimitDecision tryConsume(String policyName, String key) {
        RateLimitProperties.Policy policy = properties.policy(policyName);
        String bucketKey = policyName + ":" + key;
        Bucket bucket = buckets.get(bucketKey, ignored -> createBucket(policy));
        boolean allowed = bucket.tryConsume(1);
        long retryAfterSeconds = allowed ? 0L : Math.max(1L, policy.window().toSeconds());
        return new RateLimitDecision(allowed, retryAfterSeconds, policyName, key);
    }

    /**
     * Verifies that the given policy name is configured. Intended to be called eagerly (e.g. during filter
     * construction) so that misconfiguration fails fast at application startup rather than on the first
     * matching request.
     *
     * @throws IllegalArgumentException if the policy is not configured
     */
    public void assertPolicyConfigured(String policyName) {
        properties.policy(policyName);
    }

    private Bucket createBucket(RateLimitProperties.Policy policy) {
        Bandwidth bandwidth = Bandwidth.builder()
                .capacity(policy.capacity())
                .refillIntervally(policy.capacity(), policy.window())
                .build();
        return Bucket.builder().addLimit(bandwidth).build();
    }

    public record RateLimitDecision(boolean allowed, long retryAfterSeconds, String policyName, String key) {
    }
}
