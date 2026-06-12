package com.github.nramc.dev.journey.api.notification;

import com.github.nramc.dev.journey.api.account.AccountActivatedEvent;
import com.github.nramc.dev.journey.api.account.AccountActivationEmailRequestedEvent;
import com.github.nramc.dev.journey.api.account.EmailCodeRequestedEvent;
import com.github.nramc.dev.journey.api.account.UserRegisteredEvent;
import com.github.nramc.dev.journey.api.shared.event.JourneyAnniversaryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;

/**
 * Listens to application events published by the {@code account} module and
 * dispatches them to the appropriate notification channels.
 *
 * <p>Methods are annotated with {@link ApplicationModuleListener} which is a composed
 * annotation of {@code @Async + @TransactionalEventListener(phase = AFTER_COMMIT)},
 * guaranteeing delivery only after the originating transaction commits and running the
 * handler asynchronously.
 */
@Slf4j
@RequiredArgsConstructor
public class NotificationEventHandler {
    private final NotificationEventDispatcher notificationEventDispatcher;

    /**
     * Sends admin notifications when a new user registers.
     */
    @ApplicationModuleListener
    void onUserRegistered(UserRegisteredEvent event) {
        log.debug("Handling UserRegisteredEvent for user: {}", event.username());
        notificationEventDispatcher.dispatch(event);
    }

    /**
     * Sends the account-activation e-mail to the newly registered user.
     */
    @ApplicationModuleListener
    void onActivationEmailRequested(AccountActivationEmailRequestedEvent event) {
        log.debug("Sending activation email to: {}", event.username());
        notificationEventDispatcher.dispatch(event);
    }

    /**
     * Sends admin notifications when a user activates their account.
     */
    @ApplicationModuleListener
    void onAccountActivated(AccountActivatedEvent event) {
        log.debug("Handling AccountActivatedEvent for user: {}", event.username());
        notificationEventDispatcher.dispatch(event);
    }

    /**
     * Send email code to requested end user.
     *
     * @param event dedicated event with metadata
     */
    @ApplicationModuleListener
    void onAccountActivated(EmailCodeRequestedEvent event) {
        log.debug("Handling EmailCodeRequestedEvent for user: {}", event.username());
        notificationEventDispatcher.dispatch(event);
    }

    /**
     * Sends a single anniversary digest e-mail to the affected user for the given date.
     */
    @ApplicationModuleListener
    void onJourneyAnniversary(JourneyAnniversaryEvent event) {
        log.debug("Handling JourneyAnniversaryEvent for user: {}", event.username());
        notificationEventDispatcher.dispatch(event);
    }
}
