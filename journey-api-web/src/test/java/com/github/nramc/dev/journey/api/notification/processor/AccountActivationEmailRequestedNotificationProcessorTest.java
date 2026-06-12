package com.github.nramc.dev.journey.api.notification.processor;

import com.github.nramc.dev.journey.api.account.AccountActivationEmailRequestedEvent;
import com.github.nramc.dev.journey.api.notification.NotificationData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AccountActivationEmailRequestedNotificationProcessorTest {

    private final AccountActivationEmailRequestedNotificationProcessor processor =
            new AccountActivationEmailRequestedNotificationProcessor();

    @Nested
    class EventType {

        @Test
        void shouldSupportAccountActivationEmailRequestedEvent() {
            assertThat(processor.type()).isEqualTo(AccountActivationEmailRequestedEvent.class);
        }
    }

    @Nested
    class Create {

        @Test
        void shouldBuildEmailTemplateNotification() {
            var event = new AccountActivationEmailRequestedEvent(
                    "john@example.com",
                    "John",
                    "https://journey.codewithram.dev/activate?token=abc"
            );

            var result = processor.process(event);

            assertThat(result).isPresent();
            var data = result.orElseThrow();
            assertThat(data.subject()).isEqualTo("Journey: Activate your account");
            assertThat(data.recipients()).containsExactly("john@example.com");
            assertThat(data.type()).isEqualTo(NotificationData.NotificationType.EMAIL_ONLY);
            assertThat(data.metadata()).containsEntry("template", "account-activation-template.html");

            @SuppressWarnings("unchecked")
            var placeholders = (Map<String, Object>) data.metadata().get("metadata");
            assertThat(placeholders)
                    .containsEntry("name", "John")
                    .containsEntry("activationUrl", "https://journey.codewithram.dev/activate?token=abc");
        }
    }
}
