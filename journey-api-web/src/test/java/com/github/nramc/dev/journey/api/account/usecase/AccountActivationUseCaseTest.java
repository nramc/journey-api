package com.github.nramc.dev.journey.api.account.usecase;

import com.github.nramc.dev.journey.api.account.AccountActivatedEvent;
import com.github.nramc.dev.journey.api.account.AccountActivationEmailRequestedEvent;
import com.github.nramc.dev.journey.api.account.codes.token.EmailTokenUseCase;
import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import com.github.nramc.dev.journey.api.account.repository.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.infrastructure.actuator.ApplicationProperties;
import com.github.nramc.dev.journey.api.shared.domain.AppUser;
import com.github.nramc.dev.journey.api.shared.domain.user.security.EmailToken;
import com.github.nramc.dev.journey.api.shared.domain.user.security.Role;
import com.github.nramc.dev.journey.api.shared.exceptions.BusinessException;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Set;

import static com.github.nramc.dev.journey.api.account.web.users.UsersData.AUTHENTICATED_USER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
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
    private AuthUserDetailsService userDetailsService;
    @Mock
    private ApplicationEventPublisher applicationEvents;

    private AccountActivationUseCase accountActivationUseCase;

    @BeforeEach
    void setUp() {
        accountActivationUseCase = new AccountActivationUseCase(
                applicationProperties, emailTokenUseCase,
                userDetailsService, applicationEvents);
    }

    @Test
    void sendActivationEmail_shouldGenerateEmailToken_andPublishEvent() {
        when(applicationProperties.uiAppUrl()).thenReturn(JOURNEY_UI_BASE_URL);
        when(emailTokenUseCase.generateEmailToken(ONBOARDING_USER)).thenReturn(EMAIL_TOKEN);

        accountActivationUseCase.sendActivationEmail(ONBOARDING_USER);

        verify(applicationEvents).publishEvent(any(AccountActivationEmailRequestedEvent.class));
    }

    @Test
    void activateAccount_whenTokenNotExistsOrInvalid_shouldThrowError() {
        when(emailTokenUseCase.verifyEmailToken(EMAIL_TOKEN, ONBOARDING_USER)).thenReturn(false);

        assertThatThrownBy(() -> accountActivationUseCase.activateAccount(EMAIL_TOKEN, ONBOARDING_USER)).asInstanceOf(InstanceOfAssertFactories.throwable(BusinessException.class));
    }

    @Test
    void activateAccount_whenTokenValid_shouldActivateAccount() {
        when(emailTokenUseCase.verifyEmailToken(EMAIL_TOKEN, ONBOARDING_USER)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(ONBOARDING_USER.username())).thenReturn(ONBOARDING_USER_ENTITY);

        accountActivationUseCase.activateAccount(EMAIL_TOKEN, ONBOARDING_USER);
        verify(userDetailsService).updateUser(argThat(entity -> entity.isEnabled() && USERNAME.equals(entity.getUsername())));
        verify(applicationEvents).publishEvent(any(AccountActivatedEvent.class));
    }

}
