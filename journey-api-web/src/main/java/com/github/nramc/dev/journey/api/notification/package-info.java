/**
 * Notification module — admin notification channels (email, Telegram).
 *
 * <p>{@link com.github.nramc.dev.journey.api.notification.NotificationService} is the
 * public API for other modules that need to send admin notifications.
 *
 * <p>Listens to application events from {@code account} and routes them to the
 * appropriate notification channel via
 * {@link com.github.nramc.dev.journey.api.notification.NotificationEventHandler}.
 *
 * <p>Depends on {@code shared} (for event types and {@code AdminEmailProvider}).
 * Also has a dependency on {@code account} for the event records it listens to.
 */
@ApplicationModule(
        displayName = "Notification",
        allowedDependencies = {"shared", "account"}
)
package com.github.nramc.dev.journey.api.notification;

import org.springframework.modulith.ApplicationModule;
