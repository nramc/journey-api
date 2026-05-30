package com.github.nramc.dev.journey.api.notification;

import com.github.nramc.dev.journey.api.account.AccountActivatedEvent;
import com.github.nramc.dev.journey.api.account.AccountActivationEmailRequestedEvent;
import com.github.nramc.dev.journey.api.account.UserRegisteredEvent;
import com.github.nramc.dev.journey.api.notification.mail.MailService;
import jakarta.mail.MessagingException;
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

    // todo: remove direct dependency with mail service
    private final MailService mailService;
    private final List<NotificationService> notificationServices;

    /**
     * Sends admin notifications when a new user registers.
     */
    @ApplicationModuleListener
    void onUserRegistered(UserRegisteredEvent event) {
        log.debug("Handling UserRegisteredEvent for user: {}", event.username());
        notificationServices.forEach(svc ->
                svc.notify("New User signup - " + event.username()));
    }

    /**
     * Sends the account-activation e-mail to the newly registered user.
     */
    @ApplicationModuleListener
    void onActivationEmailRequested(AccountActivationEmailRequestedEvent event) {
        log.debug("Sending activation email to: {}", event.username());
        try {
            Map<String, Object> placeholders = new HashMap<>();
            placeholders.put("name", event.name());
            placeholders.put("activationUrl", event.activationUrl());
            mailService.sendEmailUsingTemplate(
                    ACTIVATION_EMAIL_TEMPLATE,
                    event.username(),
                    "Journey: Activate your account",
                    placeholders);
        } catch (MessagingException ex) {
            log.error("Failed to send activation email to {}", event.username(), ex);
        }
    }

    /**
     * Sends admin notifications when a user activates their account.
     */
    @ApplicationModuleListener
    void onAccountActivated(AccountActivatedEvent event) {
        log.debug("Handling AccountActivatedEvent for user: {}", event.username());
        notificationServices.forEach(svc ->
                svc.notify("User completed onboarding - " + event.username()));
    }
}
