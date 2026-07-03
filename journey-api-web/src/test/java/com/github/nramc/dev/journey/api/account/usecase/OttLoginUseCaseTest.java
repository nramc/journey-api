package com.github.nramc.dev.journey.api.account.usecase;

import com.github.nramc.dev.journey.api.account.web.auth.dto.LoginResponse;
import com.github.nramc.dev.journey.api.account.web.auth.provider.JwtResponseProvider;
import com.github.nramc.dev.journey.api.shared.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.ott.DefaultOneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.Instant;

import static com.github.nramc.dev.journey.api.account.web.users.UsersData.AUTHENTICATED_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OttLoginUseCaseTest {
    private static final String TOKEN_VALUE = "2fbbd48c-c16a-4638-9bce-988502cc6f11";

    @Mock
    private OneTimeTokenService oneTimeTokenService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtResponseProvider jwtResponseProvider;

    private OttLoginUseCase ottLoginUseCase;

    @BeforeEach
    void setUp() {
        ottLoginUseCase = new OttLoginUseCase(oneTimeTokenService, userDetailsService, jwtResponseProvider);
    }

    @Test
    @SuppressWarnings("java:S8692")
        // for not using Clock
    void login_whenTokenValid_shouldReturnJwtResponse() {
        when(oneTimeTokenService.consume(any(OneTimeTokenAuthenticationToken.class)))
                .thenReturn(new DefaultOneTimeToken(TOKEN_VALUE, AUTHENTICATED_USER.getUsername(), Instant.now().plusSeconds(900)));
        when(userDetailsService.loadUserByUsername(AUTHENTICATED_USER.getUsername())).thenReturn(AUTHENTICATED_USER);
        LoginResponse expectedResponse = LoginResponse.builder().token("jwt-token").build();
        when(jwtResponseProvider.jwtResponse(AUTHENTICATED_USER)).thenReturn(expectedResponse);

        LoginResponse response = ottLoginUseCase.login(TOKEN_VALUE);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void login_whenTokenInvalidOrExpired_shouldThrowError() {
        when(oneTimeTokenService.consume(any(OneTimeTokenAuthenticationToken.class))).thenReturn(null);

        assertThatThrownBy(() -> ottLoginUseCase.login(TOKEN_VALUE)).isInstanceOf(BusinessException.class);
    }

}

