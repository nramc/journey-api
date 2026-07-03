package com.github.nramc.dev.journey.api.notification.processor;

import com.github.nramc.dev.journey.api.account.PasswordRecoveryRequestedEvent;
import com.github.nramc.dev.journey.api.notification.NotificationData;
import com.github.nramc.dev.journey.api.notification.NotificationEventProcessor;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PasswordRecoveryRequestedNotificationProcessor
        implements NotificationEventProcessor<PasswordRecoveryRequestedEvent> {

    private static final String RECOVERY_EMAIL_TEMPLATE = "password-recovery-template.html";

    @Override
    public @NonNull Class<PasswordRecoveryRequestedEvent> type() {
        return PasswordRecoveryRequestedEvent.class;
    }

    @Override
    public @NonNull Optional<NotificationData> process(@NonNull PasswordRecoveryRequestedEvent event) {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("name", event.name());
        placeholders.put("recoveryUrl", event.recoveryUrl());

        return Optional.of(NotificationData.ofEmail(
                "Journey: Account Recovery Request",
                List.of(event.username()),
                RECOVERY_EMAIL_TEMPLATE,
                placeholders
        ));
    }
}
