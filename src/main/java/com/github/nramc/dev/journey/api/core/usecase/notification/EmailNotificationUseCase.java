package com.github.nramc.dev.journey.api.core.usecase.notification;

import com.github.nramc.dev.journey.api.core.services.mail.MailService;
import com.github.nramc.dev.journey.api.core.services.user.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@RequiredArgsConstructor
public class EmailNotificationUseCase {
    private static final String SUBJECT_PREFIX = "Notification: ";
    private final MailService mailService;
    private final AuthUserDetailsService authUserDetailsService;

    public void notifyAdmin(String notificationText) {
        List<AuthUser> admins = authUserDetailsService.findAllAdministratorUsers();
        List<String> adminEmailAddresses = CollectionUtils.emptyIfNull(admins).stream().map(AuthUser::getUsername).toList();
        mailService.sendSimpleEmail(adminEmailAddresses, SUBJECT_PREFIX + notificationText, SUBJECT_PREFIX + notificationText);
    }

}
