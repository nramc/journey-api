package com.github.nramc.dev.journey.api.core.usecase.notification;

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
     * @param message the body of the notification
     */
    void notify(String message);

    /**
     * Sends an error alert to the channel.
     *
     * @param message a short description of the error
     */
    void notifyError(String message);
}
