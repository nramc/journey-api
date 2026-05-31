package com.github.nramc.dev.journey.api.notification;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder(toBuilder = true)
public record NotificationData(
        String subject,
        String message,
        List<String> recipients,
        Map<String, Object> metadata,
        NotificationType type
) {
    public enum NotificationType { ALL, EMAIL_ONLY, TELEGRAM_ONLY }

    public static NotificationData of(String message) {
        return ofMessage(message);
    }

    public static NotificationData ofMessage(String message) {
        return NotificationData.builder()
                .message(message)
                .type(NotificationType.TELEGRAM_ONLY)
                .build();
    }

    public static NotificationData ofEmail(String subject, String message, List<String> recipients) {
        return NotificationData.builder()
                .subject(subject)
                .message(message)
                .recipients(recipients)
                .type(NotificationType.EMAIL_ONLY)
                .build();
    }

    public static NotificationData ofEmail(String subject, List<String> recipients, String template, Map<String, Object> placeholders) {
        return NotificationData.builder()
                .subject(subject)
                .message("")// message is not used for email notifications, but we can set it to an empty string to avoid nulls
                .recipients(recipients)
                .type(NotificationType.EMAIL_ONLY)
                .metadata(Map.of("template", template, "metadata", placeholders))
                .build();
    }
}
