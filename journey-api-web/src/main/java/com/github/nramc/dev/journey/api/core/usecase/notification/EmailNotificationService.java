package com.github.nramc.dev.journey.api.core.usecase.notification;

import com.github.nramc.dev.journey.api.core.services.mail.MailService;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.AuthUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Email-based implementation of {@link NotificationService}.
 *
 * <p>Resolves all administrator users from the database and sends them
 * a plain-text email for each notification type.
 */
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService implements NotificationService {

    private static final String SUBJECT_PREFIX = "Notification: ";
    private static final String ERROR_SUBJECT_PREFIX = "Error Alert: ";

    private final MailService mailService;
    private final AuthUserDetailsService authUserDetailsService;

    @Override
    public void notify(String message) {
        List<String> adminEmails = resolveAdminEmails();
        if (CollectionUtils.isNotEmpty(adminEmails)) {
            mailService.sendSimpleEmail(adminEmails,
                    SUBJECT_PREFIX + message,
                    SUBJECT_PREFIX + message);
            log.debug("Admin notification email sent to {} recipient(s)", adminEmails.size());
        }
    }

    @Override
    public void notifyError(String message) {
        List<String> adminEmails = resolveAdminEmails();
        if (CollectionUtils.isNotEmpty(adminEmails)) {
            mailService.sendSimpleEmail(adminEmails,
                    ERROR_SUBJECT_PREFIX + message,
                    ERROR_SUBJECT_PREFIX + message);
            log.debug("Error alert email sent to {} recipient(s)", adminEmails.size());
        }
    }

    private List<String> resolveAdminEmails() {
        List<AuthUser> admins = authUserDetailsService.findAllAdministratorUsers();
        return CollectionUtils.emptyIfNull(admins).stream()
                .map(AuthUser::getUsername)
                .toList();
    }
}
