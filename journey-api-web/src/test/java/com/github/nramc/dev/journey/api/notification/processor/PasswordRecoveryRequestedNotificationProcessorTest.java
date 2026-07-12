package com.github.nramc.dev.journey.api.notification.processor;

import com.github.nramc.dev.journey.api.account.PasswordRecoveryRequestedEvent;
import com.github.nramc.dev.journey.api.notification.NotificationData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordRecoveryRequestedNotificationProcessorTest {

    private final PasswordRecoveryRequestedNotificationProcessor processor = new PasswordRecoveryRequestedNotificationProcessor();

    @Nested
    class EventType {

        @Test
        void shouldSupportPasswordRecoveryRequestedEvent() {
            assertThat(processor.type()).isEqualTo(PasswordRecoveryRequestedEvent.class);
        }
    }

    @Nested
    class Create {

        @Test
        void shouldBuildPasswordRecoveryEmailTemplateNotification() {
            var event = new PasswordRecoveryRequestedEvent(
                    "john@example.com",
                    "John",
                    "https://journey.codewithram.dev/account/recover/login?token=abc123"
            );

            var result = processor.process(event);

            assertThat(result).isPresent();
            var data = result.orElseThrow();
            assertThat(data.subject()).isEqualTo("Journey: Account Recovery Request");
            assertThat(data.recipients()).containsExactly("john@example.com");
            assertThat(data.type()).isEqualTo(NotificationData.NotificationType.EMAIL_ONLY);
            assertThat(data.metadata()).containsEntry("template", "password-recovery-template.html");

            @SuppressWarnings("unchecked")
            var placeholders = (Map<String, Object>) data.metadata().get("metadata");
            assertThat(placeholders)
                    .containsEntry("name", "John")
                    .containsEntry("recoveryUrl", "https://journey.codewithram.dev/account/recover/login?token=abc123");
        }
    }
}
