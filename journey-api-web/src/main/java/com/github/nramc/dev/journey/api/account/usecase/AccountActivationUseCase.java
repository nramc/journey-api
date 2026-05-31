package com.github.nramc.dev.journey.api.account.usecase;

import com.github.nramc.dev.journey.api.account.AccountActivatedEvent;
import com.github.nramc.dev.journey.api.account.AccountActivationEmailRequestedEvent;
import com.github.nramc.dev.journey.api.account.codes.token.EmailTokenUseCase;
import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import com.github.nramc.dev.journey.api.account.repository.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.infrastructure.actuator.ApplicationProperties;
import com.github.nramc.dev.journey.api.shared.domain.AppUser;
import com.github.nramc.dev.journey.api.shared.domain.user.security.EmailToken;
import com.github.nramc.dev.journey.api.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
public class AccountActivationUseCase {

    private final ApplicationProperties applicationProperties;
    private final EmailTokenUseCase emailTokenUseCase;
    private final AuthUserDetailsService userDetailsService;
    private final ApplicationEventPublisher applicationEvents;

    @Transactional
    public void sendActivationEmail(AppUser user) {
        EmailToken emailToken = emailTokenUseCase.generateEmailToken(user);
        String activationUrl = getActivationUrl(emailToken, user);
        applicationEvents.publishEvent(
                new AccountActivationEmailRequestedEvent(user.username(), user.name(), activationUrl));
    }

    @Transactional
    public void activateAccount(EmailToken emailToken, AppUser user) {
        if (emailTokenUseCase.verifyEmailToken(emailToken, user)) {
            activate(user);
        } else {
            throw new BusinessException("", "token.invalid.not.exists");
        }
    }

    private void activate(AppUser user) {
        AuthUser userEntity = (AuthUser) userDetailsService.loadUserByUsername(user.username());
        AuthUser updatedUserEntity = userEntity.toBuilder().enabled(true).build();
        userDetailsService.updateUser(updatedUserEntity);
        applicationEvents.publishEvent(new AccountActivatedEvent(userEntity.getUsername()));
    }

    private String getActivationUrl(EmailToken emailToken, AppUser user) {
        return UriComponentsBuilder.fromUriString(applicationProperties.uiAppUrl())
                .path("/activation")
                .queryParam("identifier", user.username())
                .queryParam("token", emailToken.token())
                .build().toUriString();
    }
}
