package com.github.nramc.dev.journey.api.infrastructure.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import java.time.Duration;

public class RateLimiterService {
    private final Cache<String, Bucket> buckets;

    public RateLimiterService() {
        this.buckets = Caffeine.newBuilder()
                .maximumSize(100_000)
                .expireAfterAccess(Duration.ofHours(1))
                .build();
    }

    public RateLimitDecision tryConsume(RateLimitProperties.Policy policy, String key) {
        String bucketKey = policy.name() + ":" + key;
        Bucket bucket = buckets.get(bucketKey, ignored -> createBucket(policy));
        boolean allowed = bucket.tryConsume(1);
        long retryAfterSeconds = allowed ? 0L : Math.max(1L, policy.window().toSeconds());
        return new RateLimitDecision(allowed, retryAfterSeconds, policy.name(), key);
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
