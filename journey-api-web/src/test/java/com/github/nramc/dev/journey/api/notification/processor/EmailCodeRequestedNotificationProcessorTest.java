package com.github.nramc.dev.journey.api.notification.processor;

import com.github.nramc.dev.journey.api.account.EmailCodeRequestedEvent;
import com.github.nramc.dev.journey.api.notification.NotificationData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EmailCodeRequestedNotificationProcessorTest {

    private final EmailCodeRequestedNotificationProcessor processor = new EmailCodeRequestedNotificationProcessor();

    @Nested
    class EventType {

        @Test
        void shouldSupportEmailCodeRequestedEvent() {
            assertThat(processor.type()).isEqualTo(EmailCodeRequestedEvent.class);
        }
    }

    @Nested
    class Create {

        @Test
        void shouldBuildEmailCodeTemplateNotification() {
            var event = new EmailCodeRequestedEvent(
                    "john@example.com",
                    Map.of("name", "John", "ottPin", "123456")
            );

            var result = processor.process(event);

            assertThat(result).isPresent();
            var data = result.orElseThrow();
            assertThat(data.subject()).isEqualTo("Journey: Confirmation Required");
            assertThat(data.recipients()).containsExactly("john@example.com");
            assertThat(data.type()).isEqualTo(NotificationData.NotificationType.EMAIL_ONLY);
            assertThat(data.metadata()).containsEntry("template", "email-code-template.html");

            @SuppressWarnings("unchecked")
            var placeholders = (Map<String, Object>) data.metadata().get("metadata");
            assertThat(placeholders)
                    .containsEntry("name", "John")
                    .containsEntry("ottPin", "123456");
        }
    }
}
