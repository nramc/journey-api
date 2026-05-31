package com.github.nramc.dev.journey.api.notification.email;

import com.github.nramc.dev.journey.api.notification.NotificationData;
import com.github.nramc.dev.journey.api.notification.mail.EmailNotificationService;
import com.github.nramc.dev.journey.api.notification.mail.MailSender;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static com.github.nramc.dev.journey.api.notification.NotificationData.NotificationType.EMAIL_ONLY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.assertArg;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {
    private static final NotificationData EMAIL_NOTIFICATION_DATA = NotificationData.builder()
            .type(EMAIL_ONLY)
            .subject("Test Subject")
            .message("Test Message")
            .recipients(List.of("admin@example.com"))
            .build();
    @Mock
    private MailSender mailSender;
    @InjectMocks
    private EmailNotificationService emailNotificationService;

    @Test
    void contextTest_shouldInitialiseCorrectly() {
        assertThat(mailSender).isNotNull();
        assertThat(emailNotificationService).isNotNull();
    }

    @Test
    void notifyAdmin_whenExists_shouldSendEmailToAllAdmins() {

        emailNotificationService.notify(EMAIL_NOTIFICATION_DATA);

        verify(mailSender).sendSimpleEmail(
                assertArg(emails -> assertThat(emails).hasSize(1).contains("admin@example.com")),
                eq("Test Subject"),
                eq("Test Message")
        );
    }

    @Test
    void notify_whenMultipleAdminsExist_shouldSendEmailToAllOfThem() {
        var recipients = List.of("recipient1@example.com", "recipient2@example.com");
        var notificationData = EMAIL_NOTIFICATION_DATA.toBuilder().recipients(recipients).build();
        emailNotificationService.notify(notificationData);

        verify(mailSender).sendSimpleEmail(recipients, "Test Subject", "Test Message");
    }

    @Test
    void notify_withBlankMessage_shouldThrowException() {
        var invalid = EMAIL_NOTIFICATION_DATA.toBuilder().message("").build();
        assertThatThrownBy(() -> emailNotificationService.notify(invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Both message and template cannot be null or empty");
    }

    @Test
    void notify_withBlankSubject_shouldThrowException() {
        var invalid = EMAIL_NOTIFICATION_DATA.toBuilder().subject("").build();
        assertThatThrownBy(() -> emailNotificationService.notify(invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Notification subject cannot be null or blank");
    }

    @Test
    void notify_withEmptyRecipients_shouldThrowException() {
        var invalid = EMAIL_NOTIFICATION_DATA.toBuilder().recipients(List.of()).build();
        assertThatThrownBy(() -> emailNotificationService.notify(invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Notification recipients cannot be null or blank");
    }

    @Test
    void notify_withUnsupportedNotificationType_shouldNotSendEmail() {
        var unsupported = EMAIL_NOTIFICATION_DATA.toBuilder()
                .type(NotificationData.NotificationType.TELEGRAM_ONLY)
                .build();
        emailNotificationService.notify(unsupported);
        verify(mailSender, org.mockito.Mockito.never()).sendSimpleEmail(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString()
        );
    }

    @Test
    void notify_withTemplateEmail_shouldSendTemplateEmail() throws MessagingException {
        var notificationData = EMAIL_NOTIFICATION_DATA.toBuilder()
                .metadata(Map.of("template", "welcome-email", "metadata", Map.of("user", "Alice")))
                .build();
        emailNotificationService.notify(notificationData);
        verify(mailSender).sendEmailUsingTemplate(
                "welcome-email",
                notificationData.recipients(),
                notificationData.subject(),
                Map.of("user", "Alice")
        );
    }

}
