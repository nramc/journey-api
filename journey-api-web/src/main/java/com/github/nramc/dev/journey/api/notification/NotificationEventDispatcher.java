package com.github.nramc.dev.journey.api.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Resolves event-specific notification processors and dispatches produced
 * notifications to all configured notification channels.
 */
@RequiredArgsConstructor
@Slf4j
public class NotificationEventDispatcher {

    private final List<NotificationService> notificationServices;
    private final List<NotificationEventProcessor<?>> notificationEventProcessors;

    public void dispatch(@NonNull Object event) {
        resolve(event)
                .flatMap(processor -> process(processor, event))
                .ifPresent(this::publish);
    }

    private Optional<NotificationEventProcessor<?>> resolve(Object event) {
        return notificationEventProcessors.stream()
                .filter(processor -> processor.type().isInstance(event))
                .findFirst();
    }

    @SuppressWarnings("unchecked")
    private Optional<NotificationData> process(NotificationEventProcessor<?> processor, Object event) {
        NotificationEventProcessor<Object> typedProcessor = (NotificationEventProcessor<Object>) processor;
        return typedProcessor.process(event);
    }

    private void publish(NotificationData notificationData) {
        notificationServices.forEach(service -> service.notify(notificationData));
        log.debug("Dispatched notification to {} channel(s)", notificationServices.size());
    }
}
