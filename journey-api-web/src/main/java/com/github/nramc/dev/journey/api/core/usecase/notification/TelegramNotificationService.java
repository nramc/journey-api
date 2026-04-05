package com.github.nramc.dev.journey.api.core.usecase.notification;

import com.github.nramc.dev.journey.api.gateway.telegram.TelegramGateway;
import com.github.nramc.dev.journey.api.gateway.telegram.TelegramProperties.ParseMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Use-case service for sending notifications to the application's Telegram channel.
 *
 * <p>This bean is only present in the Spring context when
 * {@code service.telegram.enabled=true}. Callers that want to use Telegram
 * notifications optionally should inject {@code Optional<TelegramNotificationService>}.
 *
 * <p>All message methods delegate to {@link TelegramGateway} which handles
 * error-logging without propagation.
 */
@RequiredArgsConstructor
@Slf4j
public class TelegramNotificationService {

    private final TelegramGateway telegramGateway;

    /**
     * Sends a plain notification message to the channel.
     *
     * @param message the text to send (HTML formatting supported by default)
     */
    public void sendNotification(String message) {
        log.debug("Sending Telegram notification: {}", message);
        telegramGateway.sendMessage(message);
    }

    /**
     * Sends a notification with an explicit Telegram parse mode.
     *
     * @param message   the text to send
     * @param parseMode the formatting mode; use {@link ParseMode#NONE} for plain text
     */
    public void sendNotification(String message, ParseMode parseMode) {
        log.debug("Sending Telegram notification with parseMode={}: {}", parseMode, message);
        telegramGateway.sendMessage(message, parseMode);
    }

    /**
     * Sends a formatted admin alert to the channel.
     *
     * <p>The message is wrapped in a standard alert template with a bell emoji
     * and bold header so it stands out in the channel feed.
     *
     * @param notificationText the body of the alert
     */
    public void notifyAdmin(String notificationText) {
        String formatted = """
                🔔 <b>Admin Notification</b>
                
                %s
                """.formatted(notificationText);
        telegramGateway.sendMessage(formatted);
    }

    /**
     * Sends an error alert to the channel.
     *
     * @param errorSummary a short description of the error
     */
    public void notifyError(String errorSummary) {
        String formatted = """
                🚨 <b>Error Alert</b>
                
                %s
                """.formatted(errorSummary);
        telegramGateway.sendMessage(formatted);
    }
}

