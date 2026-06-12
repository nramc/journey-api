package com.github.nramc.dev.journey.api.notification.processor;

import com.github.nramc.dev.journey.api.account.AccountActivatedEvent;
import com.github.nramc.dev.journey.api.notification.NotificationData;
import com.github.nramc.dev.journey.api.notification.NotificationEventProcessor;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class AccountActivatedNotificationProcessor implements NotificationEventProcessor<AccountActivatedEvent> {

    @Override
    public @NonNull Class<AccountActivatedEvent> type() {
        return AccountActivatedEvent.class;
    }

    @Override
    public @NonNull Optional<NotificationData> process(@NonNull AccountActivatedEvent event) {
        return Optional.of(NotificationData.of("User completed onboarding - " + event.username()));
    }
}
