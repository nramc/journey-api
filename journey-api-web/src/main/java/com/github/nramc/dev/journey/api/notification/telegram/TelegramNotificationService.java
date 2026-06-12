package com.github.nramc.dev.journey.api.notification.telegram;

import com.github.nramc.dev.journey.api.notification.NotificationData;
import com.github.nramc.dev.journey.api.notification.NotificationService;
import com.github.nramc.dev.journey.api.notification.telegram.TelegramProperties.ParseMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static com.github.nramc.dev.journey.api.notification.NotificationData.NotificationType.ALL;
import static com.github.nramc.dev.journey.api.notification.NotificationData.NotificationType.TELEGRAM_ONLY;

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
public class TelegramNotificationService implements NotificationService {

    private final TelegramGateway telegramGateway;
    private final TelegramProperties telegramProperties;

    private boolean canSkip(NotificationData notificationData) {
        return !(telegramProperties.enabled() && List.of(ALL, TELEGRAM_ONLY).contains(notificationData.type()));
    }

    /**
     * Sends a plain notification message to the channel.
     *
     * @param notificationData the notification data, including a message and optional metadata
     */
    public void sendNotification(NotificationData notificationData) {
        if (canSkip(notificationData)) {
            return;
        }

        requireValidNotificationData(notificationData);

        log.debug("Sending Telegram notification: {}", notificationData.message());
        telegramGateway.sendMessage(notificationData.message());
    }

    private void requireValidNotificationData(NotificationData notificationData) {

        if (notificationData == null) {
            throw new IllegalArgumentException("NotificationData cannot be null");
        }
        if (StringUtils.isBlank(notificationData.message())) {
            throw new IllegalArgumentException("Notification message cannot be null or blank");
        }
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
     * @param notificationData the notification data, including a message and optional metadata
     */
    @Override
    public void notify(@NonNull NotificationData notificationData) {
        if (canSkip(notificationData)) {
            return;
        }

        requireValidNotificationData(notificationData);

        String formatted = """
                🔔 <b>Admin Notification</b>
                
                %s
                """.formatted(notificationData.message());
        telegramGateway.sendMessage(formatted);
    }

}
