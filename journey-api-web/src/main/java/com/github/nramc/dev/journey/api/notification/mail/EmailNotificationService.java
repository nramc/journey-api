package com.github.nramc.dev.journey.api.notification.mail;

import com.github.nramc.dev.journey.api.notification.NotificationData;
import com.github.nramc.dev.journey.api.notification.NotificationService;
import com.github.nramc.dev.journey.api.shared.exceptions.TechnicalException;
import com.github.nramc.dev.journey.api.shared.provider.AdminEmailProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

import static com.github.nramc.dev.journey.api.notification.NotificationData.NotificationType.ALL;
import static com.github.nramc.dev.journey.api.notification.NotificationData.NotificationType.EMAIL_ONLY;

/**
 * Email-based implementation of {@link NotificationService}.
 *
 * <p>Resolves administrator e-mail addresses via {@link AdminEmailProvider} (a shared
 * interface implemented by {@code account.repository.AuthUserDetailsService}) to avoid
 * a direct dependency on the {@code account} module.
 */
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService implements NotificationService {

    private static final String EMAIL_TEMPLATE_METADATA_KEY = "template";
    private final MailSender mailSender;

    private boolean isSupported(NotificationData notificationData) {
        return List.of(ALL, EMAIL_ONLY).contains(notificationData.type());
    }

    @Override
    public void notify(@NonNull NotificationData notificationData) {
        if (!isSupported(notificationData)) {
            return;
        }

        requireValidNotificationData(notificationData);

        if (isTemplateEmail(notificationData)) {
            sendTemplateEmail(notificationData);
        } else {
            mailSender.sendSimpleEmail(notificationData.recipients(), notificationData.subject(), notificationData.message());
            log.debug("Notification email sent to {} recipient(s)", notificationData.recipients().size());
        }
    }

    private boolean isTemplateEmail(NotificationData notificationData) {
        return notificationData.metadata() != null && notificationData.metadata().containsKey(EMAIL_TEMPLATE_METADATA_KEY);
    }

    private void sendTemplateEmail(NotificationData notificationData) {
        String template = (String) notificationData.metadata().get(EMAIL_TEMPLATE_METADATA_KEY);
        @SuppressWarnings("unchecked")
        var placeholders = (Map<String, Object>) notificationData.metadata().get("metadata");
        try {
            mailSender.sendEmailUsingTemplate(template, notificationData.recipients(), notificationData.subject(), placeholders);
            log.debug("Notification email sent to {} recipient(s) with template {}", notificationData.recipients().size(), template);
        } catch (Exception e) {
            log.error("Failed to send notification email using template[{}] to recipient: {}", template, notificationData.recipients(), e);
            throw new TechnicalException("Failed to send notification email using template: " + template, e);
        }
    }

    private void requireValidNotificationData(NotificationData notificationData) {
        if (notificationData == null) {
            throw new IllegalArgumentException("NotificationData cannot be null");
        }
        if (StringUtils.isBlank(notificationData.message())
                && StringUtils.isBlank(MapUtils.getString(notificationData.metadata(), EMAIL_TEMPLATE_METADATA_KEY))) {
            throw new IllegalArgumentException("Both message and template cannot be null or empty");
        }
        if (StringUtils.isBlank(notificationData.subject())) {
            throw new IllegalArgumentException("Notification subject cannot be null or blank");
        }
        if (CollectionUtils.isEmpty(notificationData.recipients())) {
            throw new IllegalArgumentException("Notification recipients cannot be null or blank");
        }
    }
}
