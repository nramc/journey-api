package com.github.nramc.dev.journey.api.notification.processor;

import com.github.nramc.dev.journey.api.account.AccountActivationEmailRequestedEvent;
import com.github.nramc.dev.journey.api.notification.NotificationData;
import com.github.nramc.dev.journey.api.notification.NotificationEventProcessor;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AccountActivationEmailRequestedNotificationProcessor
        implements NotificationEventProcessor<AccountActivationEmailRequestedEvent> {

    private static final String ACTIVATION_EMAIL_TEMPLATE = "account-activation-template.html";

    @Override
    public @NonNull Class<AccountActivationEmailRequestedEvent> type() {
        return AccountActivationEmailRequestedEvent.class;
    }

    @Override
    public @NonNull Optional<NotificationData> process(@NonNull AccountActivationEmailRequestedEvent event) {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("name", event.name());
        placeholders.put("activationUrl", event.activationUrl());

        return Optional.of(NotificationData.ofEmail(
                "Journey: Activate your account",
                List.of(event.username()),
                ACTIVATION_EMAIL_TEMPLATE,
                placeholders
        ));
    }
}
