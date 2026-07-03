package com.github.nramc.dev.journey.api.account.usecase;

import com.github.nramc.dev.journey.api.account.PasswordRecoveryRequestedEvent;
import com.github.nramc.dev.journey.api.account.codes.ott.OttProperties;
import com.github.nramc.dev.journey.api.infrastructure.actuator.ApplicationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.ott.DefaultOneTimeToken;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Duration;
import java.time.Instant;

import static com.github.nramc.dev.journey.api.account.web.users.UsersData.AUTHENTICATED_USER;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryUseCaseTest {
    private static final String JOURNEY_UI_BASE_URL = "https://journey.codewithram.dev";
    private static final String TOKEN_VALUE = "2fbbd48c-c16a-4638-9bce-988502cc6f11";
    private static final ApplicationProperties APPLICATION_PROPERTIES = ApplicationProperties.builder()
            .name("Journey API")
            .version("1.0.0")
            .uiAppUrl(JOURNEY_UI_BASE_URL)
            .build();
    private static final OttProperties OTT_PROPERTIES = OttProperties.builder()
            .tokenValidity(Duration.ofMinutes(15))
            .recoveryPath("/recover")
            .tokenQueryPram("token")
            .build();

    @Mock
    private OneTimeTokenService oneTimeTokenService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private ApplicationEventPublisher applicationEvents;

    private PasswordRecoveryUseCase passwordRecoveryUseCase;

    @BeforeEach
    void setUp() {
        passwordRecoveryUseCase = new PasswordRecoveryUseCase(
                APPLICATION_PROPERTIES, OTT_PROPERTIES, oneTimeTokenService, userDetailsService, applicationEvents);
    }

    @Test
    @SuppressWarnings("java:S8692")
        // Suppress Sonar warning about using system clock
    void sendRecoveryEmail_whenUserExists_shouldGenerateTokenAndPublishEvent() {
        when(userDetailsService.loadUserByUsername(AUTHENTICATED_USER.getUsername())).thenReturn(AUTHENTICATED_USER);
        when(oneTimeTokenService.generate(any(GenerateOneTimeTokenRequest.class)))
                .thenReturn(new DefaultOneTimeToken(TOKEN_VALUE, AUTHENTICATED_USER.getUsername(), Instant.now().plusSeconds(900)));

        passwordRecoveryUseCase.sendRecoveryEmail(AUTHENTICATED_USER.getUsername());

        verify(applicationEvents).publishEvent(new PasswordRecoveryRequestedEvent(
                AUTHENTICATED_USER.getUsername(), AUTHENTICATED_USER.getName(),
                JOURNEY_UI_BASE_URL + "/recover?token=" + TOKEN_VALUE));
    }

    @Test
    void sendRecoveryEmail_whenUserNotExists_shouldFailSilently() {
        when(userDetailsService.loadUserByUsername("unknown@example.com"))
                .thenThrow(new UsernameNotFoundException("not found"));

        assertThatCode(() -> passwordRecoveryUseCase.sendRecoveryEmail("unknown@example.com"))
                .doesNotThrowAnyException();

        verify(oneTimeTokenService, never()).generate(any());
        verify(applicationEvents, never()).publishEvent(any());
    }

}
