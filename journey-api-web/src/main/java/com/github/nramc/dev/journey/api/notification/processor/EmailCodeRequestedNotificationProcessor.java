package com.github.nramc.dev.journey.api.notification.processor;

import com.github.nramc.dev.journey.api.account.EmailCodeRequestedEvent;
import com.github.nramc.dev.journey.api.notification.NotificationData;
import com.github.nramc.dev.journey.api.notification.NotificationEventProcessor;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public class EmailCodeRequestedNotificationProcessor implements NotificationEventProcessor<EmailCodeRequestedEvent> {

    private static final String EMAIL_CODE_TEMPLATE = "email-code-template.html";

    @Override
    public @NonNull Class<EmailCodeRequestedEvent> type() {
        return EmailCodeRequestedEvent.class;
    }

    @Override
    public @NonNull Optional<NotificationData> process(@NonNull EmailCodeRequestedEvent event) {
        return Optional.of(NotificationData.ofEmail(
                "Journey: Confirmation Required",
                List.of(event.username()),
                EMAIL_CODE_TEMPLATE,
                event.metadata()
        ));
    }
}
