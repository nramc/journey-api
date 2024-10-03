package com.github.nramc.dev.journey.api.core.usecase.notification;

import com.github.nramc.dev.journey.api.core.services.mail.MailService;
import com.github.nramc.dev.journey.api.core.services.user.AuthUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.github.nramc.dev.journey.api.config.TestConfig.ADMIN_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailNotificationUseCaseTest {
    @Mock
    private MailService mailService;
    @Mock
    private AuthUserDetailsService authUserDetailsService;
    @InjectMocks
    private EmailNotificationUseCase emailNotificationUseCase;

    @Test
    void textContext() {
        assertThat(mailService).isNotNull();
        assertThat(emailNotificationUseCase).isNotNull();
    }

    @Test
    void notifyAdmin_whenAdminNotificationTriggered_shouldSendNotificationToAllAvailableAdmins() {
        when(authUserDetailsService.findAllAdministratorUsers()).thenReturn(List.of(ADMIN_USER));

        emailNotificationUseCase.notifyAdmin("Signup Completed");

        verify(mailService).sendSimpleEmail(
                assertArg(emails -> assertThat(emails).isNotNull().hasSize(1).contains(ADMIN_USER.getUsername())),
                eq("Notification: Signup Completed"),
                eq("Notification: Signup Completed")
        );
    }

}
