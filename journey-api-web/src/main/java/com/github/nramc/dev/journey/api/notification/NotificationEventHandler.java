package com.github.nramc.dev.journey.api.notification;

import com.github.nramc.dev.journey.api.account.AccountActivatedEvent;
import com.github.nramc.dev.journey.api.account.AccountActivationEmailRequestedEvent;
import com.github.nramc.dev.journey.api.account.EmailCodeRequestedEvent;
import com.github.nramc.dev.journey.api.account.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final String ACTIVATION_EMAIL_TEMPLATE = "account-activation-template.html";

    private final List<NotificationService> notificationServices;

    /**
     * Sends admin notifications when a new user registers.
     */
    @ApplicationModuleListener
    void onUserRegistered(UserRegisteredEvent event) {
        log.debug("Handling UserRegisteredEvent for user: {}", event.username());
        var notificationData = NotificationData.of("New User signup - " + event.username());
        notificationServices.forEach(svc -> svc.notify(notificationData));
    }

    /**
     * Sends the account-activation e-mail to the newly registered user.
     */
    @ApplicationModuleListener
    void onActivationEmailRequested(AccountActivationEmailRequestedEvent event) {
        log.debug("Sending activation email to: {}", event.username());

        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("name", event.name());
        placeholders.put("activationUrl", event.activationUrl());
        var notificationData = NotificationData.ofEmail(
                "Journey: Activate your account",
                List.of(event.username()),
                ACTIVATION_EMAIL_TEMPLATE, placeholders
        );
        notificationServices.forEach(svc -> svc.notify(notificationData));
    }

    /**
     * Sends admin notifications when a user activates their account.
     */
    @ApplicationModuleListener
    void onAccountActivated(AccountActivatedEvent event) {
        log.debug("Handling AccountActivatedEvent for user: {}", event.username());
        var notificationData = NotificationData.of("User completed onboarding - " + event.username());
        notificationServices.forEach(svc -> svc.notify(notificationData));
    }

    /**
     * Send email code to requested end user.
     *
     * @param event dedicated event with metadata
     */
    @ApplicationModuleListener
    void onAccountActivated(EmailCodeRequestedEvent event) {
        log.debug("Handling EmailCodeRequestedEvent for user: {}", event.username());
        var notificationData = NotificationData.ofEmail(
                "Journey: Confirmation Required",
                List.of(event.username()),
                "email-code-template.html",
                event.metadata()
        );
        notificationServices.forEach(svc -> svc.notify(notificationData));
    }
}
