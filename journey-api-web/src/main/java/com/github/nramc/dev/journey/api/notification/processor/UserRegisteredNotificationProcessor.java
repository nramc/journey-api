package com.github.nramc.dev.journey.api.notification.processor;

import com.github.nramc.dev.journey.api.account.UserRegisteredEvent;
import com.github.nramc.dev.journey.api.notification.NotificationData;
import com.github.nramc.dev.journey.api.notification.NotificationEventProcessor;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class UserRegisteredNotificationProcessor implements NotificationEventProcessor<UserRegisteredEvent> {

    @Override
    public @NonNull Class<UserRegisteredEvent> type() {
        return UserRegisteredEvent.class;
    }

    @Override
    public @NonNull Optional<NotificationData> process(@NonNull UserRegisteredEvent event) {
        return Optional.of(NotificationData.of("New User signup - " + event.username()));
    }
}
