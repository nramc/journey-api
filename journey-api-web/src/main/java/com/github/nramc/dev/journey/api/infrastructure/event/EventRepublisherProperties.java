package com.github.nramc.dev.journey.api.infrastructure.event;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "journey.module.infrastructure.event.republisher")
public record EventRepublisherProperties(
        int maxImmediateRetry,
        Duration leastLastRetried,
        Duration retryDelay,
        Duration minAge,
        int batchSize
) {
}
