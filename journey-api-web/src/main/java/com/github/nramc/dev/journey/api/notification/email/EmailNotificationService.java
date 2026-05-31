package com.github.nramc.dev.journey.api.notification.email;

import com.github.nramc.dev.journey.api.notification.NotificationService;
import com.github.nramc.dev.journey.api.notification.mail.MailService;
import com.github.nramc.dev.journey.api.shared.domain.EmailAddress;
import com.github.nramc.dev.journey.api.shared.provider.AdminEmailProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

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

    private static final String SUBJECT_PREFIX = "Notification: ";
    private static final String ERROR_SUBJECT_PREFIX = "Error Alert: ";

    private final MailService mailService;
    private final AdminEmailProvider adminEmailProvider;

    @Override
    public void notify(String message) {
        List<String> adminEmails = adminEmailProvider.get().stream().map(EmailAddress::value).toList();

        if (CollectionUtils.isNotEmpty(adminEmails)) {
            mailService.sendSimpleEmail(adminEmails,
                    SUBJECT_PREFIX + message,
                    SUBJECT_PREFIX + message);
            log.debug("Admin notification email sent to {} recipient(s)", adminEmails.size());
        }
    }

    @Override
    public void notifyError(String message) {
        List<String> adminEmails = adminEmailProvider.get().stream().map(EmailAddress::value).toList();
        if (CollectionUtils.isNotEmpty(adminEmails)) {
            mailService.sendSimpleEmail(adminEmails,
                    ERROR_SUBJECT_PREFIX + message,
                    ERROR_SUBJECT_PREFIX + message);
            log.debug("Error alert email sent to {} recipient(s)", adminEmails.size());
        }
    }
}
