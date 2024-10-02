package com.github.nramc.dev.journey.api.core.usecase.registration;

import com.github.nramc.dev.journey.api.core.app.ApplicationProperties;
import com.github.nramc.dev.journey.api.core.user.security.Role;
import com.github.nramc.dev.journey.api.core.model.AppUser;
import com.github.nramc.dev.journey.api.core.model.EmailToken;
import com.github.nramc.dev.journey.api.core.services.EmailTokenService;
import com.github.nramc.dev.journey.api.core.usecase.notification.EmailNotificationUseCase;
import com.github.nramc.dev.journey.api.gateway.MailService;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.web.exceptions.BusinessException;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.UserSecurityEmailAddressAttributeService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static com.github.nramc.dev.journey.api.config.TestConfig.AUTHENTICATED_USER;
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
    private static final String JOURNEY_UI_BASE_URL = "https://nramc.github.io/journeys";
    @Mock
    private ApplicationProperties applicationProperties;
    @Mock
    private EmailTokenService emailTokenService;
    @Mock
    private MailService mailService;
    @Mock
    private AuthUserDetailsService userDetailsService;
    @Mock
    private UserSecurityEmailAddressAttributeService emailAddressAttributeService;
    @Mock
    private EmailNotificationUseCase emailNotificationUseCase;
    @InjectMocks
    private AccountActivationUseCase accountActivationUseCase;

    @Test
    void sendActivationEmail_shouldGenerateEmailToken_andShouldSendEmailWithActivationLink() throws MessagingException {
        when(applicationProperties.uiAppUrl()).thenReturn(JOURNEY_UI_BASE_URL);
        when(emailTokenService.generateEmailToken(ONBOARDING_USER)).thenReturn(EMAIL_TOKEN);

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
        when(emailTokenService.isTokenExistsAndValid(EMAIL_TOKEN, ONBOARDING_USER)).thenReturn(false);

        assertThatExceptionOfType(BusinessException.class).isThrownBy(() -> accountActivationUseCase.activateAccount(EMAIL_TOKEN, ONBOARDING_USER));
    }

    @Test
    void activateAccount_whenTokenValid_shouldActivateAccount() {
        when(emailTokenService.isTokenExistsAndValid(EMAIL_TOKEN, ONBOARDING_USER)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(ONBOARDING_USER.username())).thenReturn(ONBOARDING_USER_ENTITY);

        accountActivationUseCase.activateAccount(EMAIL_TOKEN, ONBOARDING_USER);
        verify(userDetailsService).updateUser(argThat(entity -> entity.isEnabled() && USERNAME.equals(entity.getUsername())));
        verify(emailAddressAttributeService).saveSecurityEmailAddress(eq(ONBOARDING_USER_ENTITY), argThat(emailAddress -> USERNAME.equals(emailAddress.value())));
        verify(emailNotificationUseCase).notifyAdmin("User completed onboarding - " + USERNAME);
    }

}
