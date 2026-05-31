package com.github.nramc.dev.journey.api.notification;

import com.github.nramc.dev.journey.api.notification.email.EmailNotificationService;
import org.jspecify.annotations.NonNull;

/**
 * Common contract for all application notification channels.
 *
 * <p>Implementations are collected by Spring as a {@code List<NotificationService>}
 * and each enabled channel receives every notification call. Currently supported:
 * <ul>
 *   <li>{@link EmailNotificationService} – always active; emails all admin users</li>
 *   <li>{@code TelegramNotificationService} – active when {@code service.telegram.enabled=true}</li>
 * </ul>
 */
public interface NotificationService {

    /**
     * Sends an admin notification to the channel.
     *
     * @param notificationData the notification data, including a message and optional metadata
     */
    void notify(@NonNull NotificationData notificationData);

}
