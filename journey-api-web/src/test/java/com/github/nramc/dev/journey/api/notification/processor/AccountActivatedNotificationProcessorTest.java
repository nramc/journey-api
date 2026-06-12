package com.github.nramc.dev.journey.api.notification.processor;

import com.github.nramc.dev.journey.api.account.AccountActivatedEvent;
import com.github.nramc.dev.journey.api.notification.NotificationData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.github.nramc.dev.journey.api.notification.NotificationData.NotificationType.TELEGRAM_ONLY;
import static org.assertj.core.api.Assertions.assertThat;

class AccountActivatedNotificationProcessorTest {

    private final AccountActivatedNotificationProcessor processor = new AccountActivatedNotificationProcessor();

    @Nested
    class EventType {

        @Test
        void shouldSupportAccountActivatedEvent() {
            assertThat(processor.type()).isEqualTo(AccountActivatedEvent.class);
        }
    }

    @Nested
    class Create {

        @Test
        void shouldBuildOnboardingCompletionMessage() {
            var result = processor.process(new AccountActivatedEvent("john@example.com"));

            assertThat(result).isPresent().get()
                    .extracting(NotificationData::message, NotificationData::type)
                    .containsExactly("User completed onboarding - john@example.com", TELEGRAM_ONLY);
        }
    }
}
