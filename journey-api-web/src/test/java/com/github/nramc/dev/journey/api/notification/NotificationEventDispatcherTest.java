package com.github.nramc.dev.journey.api.notification;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class NotificationEventDispatcherTest {

    @Mock
    private NotificationService firstNotificationService;

    @Mock
    private NotificationService secondNotificationService;

    private final DummyEventProcessor dummyEventProcessor = new DummyEventProcessor();

    @Test
    void dispatch_shouldPublishToAllChannelsWhenProcessorProducesNotification() {
        var dispatcher = new NotificationEventDispatcher(
                List.of(firstNotificationService, secondNotificationService),
                List.of(dummyEventProcessor)
        );

        dispatcher.dispatch(new DummyEvent("hello"));

        verify(firstNotificationService).notify(NotificationData.of("dummy-hello"));
        verify(secondNotificationService).notify(NotificationData.of("dummy-hello"));
    }

    @Test
    void dispatch_shouldDoNothingWhenNoProcessorSupportsEvent() {
        var dispatcher = new NotificationEventDispatcher(
                List.of(firstNotificationService, secondNotificationService),
                List.of(dummyEventProcessor)
        );

        dispatcher.dispatch(new UnsupportedEvent("ignore"));

        verifyNoInteractions(firstNotificationService, secondNotificationService);
    }

    @Test
    void dispatch_shouldDoNothingWhenProcessorReturnsEmpty() {
        var dispatcher = new NotificationEventDispatcher(
                List.of(firstNotificationService, secondNotificationService),
                List.of(new EmptyDummyEventProcessor())
        );

        dispatcher.dispatch(new DummyEvent("hello"));

        verifyNoInteractions(firstNotificationService, secondNotificationService);
    }

    private record DummyEvent(String message) {
    }

    private record UnsupportedEvent(String message) {
    }

    private static final class DummyEventProcessor implements NotificationEventProcessor<DummyEvent> {

        @Override
        public @NonNull Class<DummyEvent> type() {
            return DummyEvent.class;
        }

        @Override
        public @NonNull Optional<NotificationData> process(@NonNull DummyEvent event) {
            return Optional.of(NotificationData.of("dummy-" + event.message()));
        }
    }

    private static final class EmptyDummyEventProcessor implements NotificationEventProcessor<DummyEvent> {

        @Override
        public @NonNull Class<DummyEvent> type() {
            return DummyEvent.class;
        }

        @Override
        public @NonNull Optional<NotificationData> process(@NonNull DummyEvent event) {
            return Optional.empty();
        }
    }
}
