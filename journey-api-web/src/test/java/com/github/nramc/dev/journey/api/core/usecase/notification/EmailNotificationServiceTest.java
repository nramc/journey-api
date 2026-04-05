package com.github.nramc.dev.journey.api.core.usecase.notification;

import com.github.nramc.dev.journey.api.core.services.mail.MailService;
import com.github.nramc.dev.journey.api.repository.user.AuthUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.ADMINISTRATOR_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {
    @Mock
    private MailService mailService;
    @Mock
    private AuthUserDetailsService authUserDetailsService;
    @InjectMocks
    private EmailNotificationService emailNotificationService;

    @Test
    void contextTest_shouldInitialiseCorrectly() {
        assertThat(mailService).isNotNull();
        assertThat(emailNotificationService).isNotNull();
    }

    @Test
    void notifyAdmin_whenExists_shouldSendEmailToAllAdmins() {
        when(authUserDetailsService.findAllAdministratorUsers()).thenReturn(List.of(ADMINISTRATOR_USER));

        emailNotificationService.notify("Signup Completed");

        verify(mailService).sendSimpleEmail(
                assertArg(emails -> assertThat(emails).isNotNull().hasSize(1).contains(ADMINISTRATOR_USER.getUsername())),
                eq("Notification: Signup Completed"),
                eq("Notification: Signup Completed")
        );
    }

    @Test
    void notify_subjectShouldContainNotificationPrefix() {
        when(authUserDetailsService.findAllAdministratorUsers()).thenReturn(List.of(ADMINISTRATOR_USER));

        emailNotificationService.notify("New user registered");

        verify(mailService).sendSimpleEmail(
                assertArg(emails -> assertThat(emails).isNotEmpty()),
                assertArg(subject -> assertThat(subject).startsWith("Notification:")),
                assertArg(body -> assertThat(body).isNotBlank())
        );
    }

    @Test
    void notify_subjectShouldContainOriginalNotificationText() {
        when(authUserDetailsService.findAllAdministratorUsers()).thenReturn(List.of(ADMINISTRATOR_USER));
        String notificationText = "New user registered: john@example.com";

        emailNotificationService.notify(notificationText);

        verify(mailService).sendSimpleEmail(
                assertArg(emails -> assertThat(emails).isNotEmpty()),
                assertArg(subject -> assertThat(subject).contains(notificationText)),
                assertArg(body -> assertThat(body).contains(notificationText))
        );
    }

    @Test
    void notify_whenNoAdminsExist_shouldNotSendAnyEmail() {
        when(authUserDetailsService.findAllAdministratorUsers()).thenReturn(Collections.emptyList());

        emailNotificationService.notify("Signup Completed");

        verifyNoInteractions(mailService);
    }

    @Test
    void notify_whenMultipleAdminsExist_shouldSendEmailToAllOfThem() {
        when(authUserDetailsService.findAllAdministratorUsers()).thenReturn(List.of(ADMINISTRATOR_USER, ADMINISTRATOR_USER));

        emailNotificationService.notify("System update");

        verify(mailService).sendSimpleEmail(
                assertArg(emails -> assertThat(emails).hasSize(2)),
                eq("Notification: System update"),
                eq("Notification: System update")
        );
    }

    @Test
    void notifyError_whenAdminExists_shouldSendErrorEmailToAllAdmins() {
        when(authUserDetailsService.findAllAdministratorUsers()).thenReturn(List.of(ADMINISTRATOR_USER));

        emailNotificationService.notifyError("MongoDB connection failed");

        verify(mailService).sendSimpleEmail(
                assertArg(emails -> assertThat(emails).isNotNull().hasSize(1).contains(ADMINISTRATOR_USER.getUsername())),
                eq("Error Alert: MongoDB connection failed"),
                eq("Error Alert: MongoDB connection failed")
        );
    }

    @Test
    void notifyError_subjectShouldContainErrorAlertPrefix() {
        when(authUserDetailsService.findAllAdministratorUsers()).thenReturn(List.of(ADMINISTRATOR_USER));

        emailNotificationService.notifyError("Disk full");

        verify(mailService).sendSimpleEmail(
                assertArg(emails -> assertThat(emails).isNotEmpty()),
                assertArg(subject -> assertThat(subject).startsWith("Error Alert:")),
                assertArg(body -> assertThat(body).isNotBlank())
        );
    }

    @Test
    void notifyError_subjectShouldContainOriginalErrorSummary() {
        when(authUserDetailsService.findAllAdministratorUsers()).thenReturn(List.of(ADMINISTRATOR_USER));
        String errorSummary = "MongoDB connection failed after 3 retries";

        emailNotificationService.notifyError(errorSummary);

        verify(mailService).sendSimpleEmail(
                assertArg(emails -> assertThat(emails).isNotEmpty()),
                assertArg(subject -> assertThat(subject).contains(errorSummary)),
                assertArg(body -> assertThat(body).contains(errorSummary))
        );
    }

    @Test
    void notifyError_whenNoAdminsExist_shouldNotSendAnyEmail() {
        when(authUserDetailsService.findAllAdministratorUsers()).thenReturn(Collections.emptyList());

        emailNotificationService.notifyError("Critical failure");

        verifyNoInteractions(mailService);
    }

}
