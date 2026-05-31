package com.github.nramc.dev.journey.api.notification.email;

import com.github.nramc.dev.journey.api.notification.mail.MailService;
import com.github.nramc.dev.journey.api.shared.domain.EmailAddress;
import com.github.nramc.dev.journey.api.shared.provider.AdminEmailProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {
    public static final List<EmailAddress> ADMIN_EMAILS = List.of(EmailAddress.valueOf("admin@example.com"));
    @Mock
    private MailService mailService;
    @Mock
    private AdminEmailProvider adminEmailProvider;
    @InjectMocks
    private EmailNotificationService emailNotificationService;

    @Test
    void contextTest_shouldInitialiseCorrectly() {
        assertThat(mailService).isNotNull();
        assertThat(emailNotificationService).isNotNull();
    }

    @Test
    void notifyAdmin_whenExists_shouldSendEmailToAllAdmins() {
        when(adminEmailProvider.get()).thenReturn(ADMIN_EMAILS);

        emailNotificationService.notify("Signup Completed");

        verify(mailService).sendSimpleEmail(
                assertArg(emails -> assertThat(emails).hasSize(1).contains("admin@example.com")),
                eq("Notification: Signup Completed"),
                eq("Notification: Signup Completed")
        );
    }

    @Test
    void notify_subjectShouldContainNotificationPrefix() {
        when(adminEmailProvider.get()).thenReturn(ADMIN_EMAILS);

        emailNotificationService.notify("New user registered");

        verify(mailService).sendSimpleEmail(
                assertArg(emails -> assertThat(emails).isNotEmpty()),
                assertArg(subject -> assertThat(subject).startsWith("Notification:")),
                assertArg(body -> assertThat(body).isNotBlank())
        );
    }

    @Test
    void notify_subjectShouldContainOriginalNotificationText() {
        when(adminEmailProvider.get()).thenReturn(ADMIN_EMAILS);
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
        when(adminEmailProvider.get()).thenReturn(Collections.emptyList());

        emailNotificationService.notify("Signup Completed");

        verifyNoInteractions(mailService);
    }

    @Test
    void notify_whenMultipleAdminsExist_shouldSendEmailToAllOfThem() {
        when(adminEmailProvider.get()).thenReturn(List.of(EmailAddress.valueOf("admin1@example.com"), EmailAddress.valueOf("admin2@example.com")));

        emailNotificationService.notify("System update");

        verify(mailService).sendSimpleEmail(
                assertArg(emails -> assertThat(emails).hasSize(2)),
                eq("Notification: System update"),
                eq("Notification: System update")
        );
    }

    @Test
    void notifyError_whenAdminExists_shouldSendErrorEmailToAllAdmins() {
        when(adminEmailProvider.get()).thenReturn(ADMIN_EMAILS);

        emailNotificationService.notifyError("MongoDB connection failed");

        verify(mailService).sendSimpleEmail(
                assertArg(emails -> assertThat(emails).hasSize(1).contains("admin@example.com")),
                eq("Error Alert: MongoDB connection failed"),
                eq("Error Alert: MongoDB connection failed")
        );
    }

    @Test
    void notifyError_subjectShouldContainErrorAlertPrefix() {
        when(adminEmailProvider.get()).thenReturn(ADMIN_EMAILS);

        emailNotificationService.notifyError("Disk full");

        verify(mailService).sendSimpleEmail(
                assertArg(emails -> assertThat(emails).isNotEmpty()),
                assertArg(subject -> assertThat(subject).startsWith("Error Alert:")),
                assertArg(body -> assertThat(body).isNotBlank())
        );
    }

    @Test
    void notifyError_subjectShouldContainOriginalErrorSummary() {
        when(adminEmailProvider.get()).thenReturn(ADMIN_EMAILS);
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
        when(adminEmailProvider.get()).thenReturn(Collections.emptyList());

        emailNotificationService.notifyError("Critical failure");

        verifyNoInteractions(mailService);
    }

}
