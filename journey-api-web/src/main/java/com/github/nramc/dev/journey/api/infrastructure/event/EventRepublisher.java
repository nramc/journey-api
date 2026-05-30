package com.github.nramc.dev.journey.api.infrastructure.event;

import org.springframework.modulith.events.EventPublication;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.modulith.events.ResubmissionOptions;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.time.Instant;

/**
 * Republish incomplete events.
 */
public class EventRepublisher {

    private final IncompleteEventPublications incompleteEventPublications;
    private final EventRepublisherProperties properties;

    public EventRepublisher(IncompleteEventPublications incompleteEventPublications, EventRepublisherProperties properties) {
        this.incompleteEventPublications = incompleteEventPublications;
        this.properties = properties;
    }

    @Scheduled(fixedDelayString = "${journey.module.infrastructure.event.republisher.retry-delay:PT1M}")
    public void republishFailedEvents() {
        incompleteEventPublications.resubmitIncompletePublications(
                ResubmissionOptions.defaults()
                        .withMinAge(properties.minAge())// only retry events that failed at least 1 minute ago
                        .withBatchSize(properties.batchSize())
                        .withFilter(this::shouldRetry)
        );
    }

    private boolean shouldRetry(EventPublication event) {
        boolean lastRetryExceedLeastTime = event.getLastResubmissionDate() != null
                && Duration.between(event.getLastResubmissionDate(), Instant.now()).toSeconds() >= properties.leastLastRetried().toSeconds();
        return event.getCompletionAttempts() <= properties.maxImmediateRetry() || lastRetryExceedLeastTime;
    }
}
