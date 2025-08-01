package com.github.nramc.dev.journey.api.core.usecase.registration;

import com.github.nramc.dev.journey.api.core.app.ApplicationProperties;
import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.domain.EmailToken;
import com.github.nramc.dev.journey.api.core.domain.user.Role;
import com.github.nramc.dev.journey.api.core.exceptions.BusinessException;
import com.github.nramc.dev.journey.api.core.services.mail.MailService;
import com.github.nramc.dev.journey.api.core.usecase.codes.token.EmailTokenUseCase;
import com.github.nramc.dev.journey.api.core.usecase.notification.EmailNotificationUseCase;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.AuthUserDetailsService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.AUTHENTICATED_USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountActivationUseCaseTest {
    private static final String USERNAME = "ikaika_kinsleyh6he@harold.bpk";
    private static final AppUser ONBOARDING_USER = AppUser.builder()
            .username(USERNAME)
            .name("Tacia Bos")
            .enabled(false)
            .roles(Set.of(Role.AUTHENTICATED_USER))
            .build();
    private static final AuthUser ONBOARDING_USER_ENTITY = AUTHENTICATED_USER.toBuilder()
            .username(USERNAME)
            .build();
    private static final EmailToken EMAIL_TOKEN = EmailToken.valueOf("2fbbd48c-c16a-4638-9bce-988502cc6f11");
    private static final String JOURNEY_UI_BASE_URL = "https://journey.codewithram.dev";
    @Mock
    private ApplicationProperties applicationProperties;
    @Mock
    private EmailTokenUseCase emailTokenUseCase;
    @Mock
    private MailService mailService;
    @Mock
    private AuthUserDetailsService userDetailsService;
    @Mock
    private EmailNotificationUseCase emailNotificationUseCase;
    @InjectMocks
    private AccountActivationUseCase accountActivationUseCase;

    @Test
    void sendActivationEmail_shouldGenerateEmailToken_andShouldSendEmailWithActivationLink() throws MessagingException {
        when(applicationProperties.uiAppUrl()).thenReturn(JOURNEY_UI_BASE_URL);
        when(emailTokenUseCase.generateEmailToken(ONBOARDING_USER)).thenReturn(EMAIL_TOKEN);

        accountActivationUseCase.sendActivationEmail(ONBOARDING_USER);

        String expectedActivationUrl = JOURNEY_UI_BASE_URL + "/activation?identifier=" + ONBOARDING_USER.username() + "&token=" + EMAIL_TOKEN.token();

        verify(mailService).sendEmailUsingTemplate(eq("account-activation-template.html"), eq(ONBOARDING_USER.username()), eq("Journey: Activate your account"),
                argThat(placeholders -> ONBOARDING_USER.name().equals(placeholders.get("name"))
                        && expectedActivationUrl.equals(placeholders.get("activationUrl"))
                )
        );
    }

    @Test
    void activateAccount_whenTokenNotExistsOrInvalid_shouldThrowError() {
        when(emailTokenUseCase.verifyEmailToken(EMAIL_TOKEN, ONBOARDING_USER)).thenReturn(false);

        assertThatExceptionOfType(BusinessException.class).isThrownBy(() -> accountActivationUseCase.activateAccount(EMAIL_TOKEN, ONBOARDING_USER));
    }

    @Test
    void activateAccount_whenTokenValid_shouldActivateAccount() {
        when(emailTokenUseCase.verifyEmailToken(EMAIL_TOKEN, ONBOARDING_USER)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(ONBOARDING_USER.username())).thenReturn(ONBOARDING_USER_ENTITY);

        accountActivationUseCase.activateAccount(EMAIL_TOKEN, ONBOARDING_USER);
        verify(userDetailsService).updateUser(argThat(entity -> entity.isEnabled() && USERNAME.equals(entity.getUsername())));
        verify(emailNotificationUseCase).notifyAdmin("User completed onboarding - " + USERNAME);
    }

}
