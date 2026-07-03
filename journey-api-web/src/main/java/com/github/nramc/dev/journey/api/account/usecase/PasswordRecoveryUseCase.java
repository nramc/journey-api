package com.github.nramc.dev.journey.api.account.usecase;

import com.github.nramc.dev.journey.api.account.PasswordRecoveryRequestedEvent;
import com.github.nramc.dev.journey.api.account.codes.ott.OttProperties;
import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import com.github.nramc.dev.journey.api.infrastructure.actuator.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * Handles account/password recovery requests using Spring Security's One-Time-Token (OTT)
 * mechanism.
 *
 * <p>Deliberately fails <strong>silently</strong> for unknown usernames — no exception is
 * thrown and no email is sent — to avoid leaking which usernames are registered
 * (OWASP user-enumeration prevention).
 */
@Slf4j
@RequiredArgsConstructor
public class PasswordRecoveryUseCase {

    private final ApplicationProperties applicationProperties;
    private final OttProperties ottProperties;
    private final OneTimeTokenService oneTimeTokenService;
    private final UserDetailsService userDetailsService;
    private final ApplicationEventPublisher applicationEvents;

    @Transactional
    public void sendRecoveryEmail(String username) {
        var userIfExists = getRequestedUserIfExists(username);
        if (userIfExists.isPresent()) {
            AuthUser authUser = userIfExists.get();
            OneTimeToken oneTimeToken = oneTimeTokenService.generate(new GenerateOneTimeTokenRequest(authUser.getUsername()));
            String recoveryUrl = getRecoveryUrl(oneTimeToken);

            applicationEvents.publishEvent(new PasswordRecoveryRequestedEvent(authUser.getUsername(), authUser.getName(), recoveryUrl));
            log.info("Account recovery email has been triggered successfully");
        } else {
            log.info("Account does not exist for username '{}'", username);
        }
    }

    private Optional<AuthUser> getRequestedUserIfExists(String username) {
        try {
            return Optional.of((AuthUser) userDetailsService.loadUserByUsername(username));
        } catch (UsernameNotFoundException ex) {
            log.debug("Account recovery requested for unknown username, ignoring silently");
            return Optional.empty();
        }
    }

    private String getRecoveryUrl(OneTimeToken oneTimeToken) {
        return UriComponentsBuilder.fromUriString(applicationProperties.uiAppUrl())
                .path(ottProperties.recoveryPath())
                .queryParam(ottProperties.tokenQueryPram(), oneTimeToken.getTokenValue())
                .build().toUriString();
    }
}

