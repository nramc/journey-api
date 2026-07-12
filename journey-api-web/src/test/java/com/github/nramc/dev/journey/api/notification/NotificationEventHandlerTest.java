package com.github.nramc.dev.journey.api.notification;

import com.github.nramc.dev.journey.api.account.AccountActivatedEvent;
import com.github.nramc.dev.journey.api.account.AccountActivationEmailRequestedEvent;
import com.github.nramc.dev.journey.api.account.EmailCodeRequestedEvent;
import com.github.nramc.dev.journey.api.account.PasswordRecoveryRequestedEvent;
import com.github.nramc.dev.journey.api.account.UserRegisteredEvent;
import com.github.nramc.dev.journey.api.shared.event.JourneyAnniversaryEvent;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationEventHandlerTest {

    @Mock
    private NotificationEventDispatcher notificationEventDispatcher;

    private NotificationEventHandler createHandler() {
        return new NotificationEventHandler(notificationEventDispatcher);
    }

    @Nested
    class UserRegisteredListener {

        @Test
        void shouldDispatchUserRegisteredEvent() {
            var event = new UserRegisteredEvent("john@example.com");

            createHandler().onUserRegistered(event);

            verify(notificationEventDispatcher).dispatch(event);
        }
    }

    @Nested
    class ActivationEmailRequestedListener {

        @Test
        void shouldDispatchActivationEmailRequestedEvent() {
            var event = new AccountActivationEmailRequestedEvent(
                    "john@example.com",
                    "John",
                    "https://journey.codewithram.dev/activate"
            );

            createHandler().onActivationEmailRequested(event);

            verify(notificationEventDispatcher).dispatch(event);
        }
    }

    @Nested
    class AccountActivatedListener {

        @Test
        void shouldDispatchAccountActivatedEvent() {
            var event = new AccountActivatedEvent("john@example.com");

            createHandler().onAccountActivated(event);

            verify(notificationEventDispatcher).dispatch(event);
        }
    }

    @Nested
    class EmailCodeRequestedListener {

        @Test
        void shouldDispatchEmailCodeRequestedEvent() {
            var event = new EmailCodeRequestedEvent("john@example.com", Map.of("ottPin", "123456"));

            createHandler().onAccountActivated(event);

            verify(notificationEventDispatcher).dispatch(event);
        }
    }

    @Nested
    class JourneyAnniversaryListener {

        @Test
        void shouldDispatchJourneyAnniversaryEvent() {
            var event = new JourneyAnniversaryEvent(
                    "john@example.com",
                    "John",
                    LocalDate.of(2026, Month.JUNE, 12),
                    List.of(new JourneyAnniversaryEvent.JourneyAnniversaryItem(
                            "journey-id",
                            "Trip to Rome",
                            LocalDate.of(2024, Month.JUNE, 12),
                            "Rome, Italy",
                            "https://img.example/hero.jpg",
                            List.of("https://img.example/1.jpg")
                    ))
            );

            createHandler().onJourneyAnniversary(event);

            verify(notificationEventDispatcher).dispatch(event);
        }
    }

    @Nested
    class PasswordRecoveryRequestedListener {

        @Test
        void shouldDispatchPasswordRecoveryRequestedEvent() {
            var event = new PasswordRecoveryRequestedEvent(
                    "john@example.com",
                    "John",
                    "https://journey.codewithram.dev/account/recover/login?token=abc123"
            );

            createHandler().onPasswordRecoveryRequested(event);

            verify(notificationEventDispatcher).dispatch(event);
        }
    }
}
