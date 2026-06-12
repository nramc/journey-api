package com.github.nramc.dev.journey.api.notification.processor;

import com.github.nramc.dev.journey.api.account.UserRegisteredEvent;
import com.github.nramc.dev.journey.api.notification.NotificationData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegisteredNotificationProcessorTest {

    private final UserRegisteredNotificationProcessor processor = new UserRegisteredNotificationProcessor();

    @Nested
    class EventType {

        @Test
        void shouldSupportUserRegisteredEvent() {
            assertThat(processor.type()).isEqualTo(UserRegisteredEvent.class);
        }
    }

    @Nested
    class Create {

        @Test
        void shouldBuildTelegramOnlyNotificationMessage() {
            var result = processor.process(new UserRegisteredEvent("john@example.com"));

            assertThat(result).isPresent();
            var data = result.orElseThrow();
            assertThat(data.message()).isEqualTo("New User signup - john@example.com");
            assertThat(data.type()).isEqualTo(NotificationData.NotificationType.TELEGRAM_ONLY);
        }
    }
}
