package com.github.nramc.dev.journey.api.account.usecase;

import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import com.github.nramc.dev.journey.api.account.web.auth.dto.LoginResponse;
import com.github.nramc.dev.journey.api.account.web.auth.provider.JwtResponseProvider;
import com.github.nramc.dev.journey.api.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticates a user using a previously issued One-Time-Token (OTT) — typically received
 * via the account recovery email — and issues a standard JWT in return, allowing the SPA
 * to immediately proceed to change the user's password.
 */
@Slf4j
@RequiredArgsConstructor
public class OttLoginUseCase {

    private final OneTimeTokenService oneTimeTokenService;
    private final UserDetailsService userDetailsService;
    private final JwtResponseProvider jwtResponseProvider;

    @Transactional
    public LoginResponse login(String token) {
        OneTimeToken oneTimeToken = oneTimeTokenService.consume(new OneTimeTokenAuthenticationToken(token));

        if (oneTimeToken == null) {
            throw new BusinessException("Token is invalid, expired or already used", "token.invalid.not.exists");
        }

        AuthUser authUser = (AuthUser) userDetailsService.loadUserByUsername(oneTimeToken.getUsername());
        log.info("User authenticated successfully via One-Time-Token");
        return jwtResponseProvider.jwtResponse(authUser);
    }
}

