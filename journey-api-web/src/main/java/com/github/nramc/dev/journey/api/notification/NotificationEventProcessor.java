package com.github.nramc.dev.journey.api.notification;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

/**
 * Processor contract to transform one event type into notification payload.
 *
 * @param <E> supported event type
 */
public interface NotificationEventProcessor<E> {

    /**
     * Type of event supported by the processor.
     *
     * @return event class this processor supports
     */
    @NonNull Class<E> type();

    /**
     * Builds a notification for the incoming event.
     *
     * @param event incoming application event
     * @return notification payload when the event should be emitted, otherwise empty
     */
    @NonNull Optional<NotificationData> process(@NonNull E event);
}
