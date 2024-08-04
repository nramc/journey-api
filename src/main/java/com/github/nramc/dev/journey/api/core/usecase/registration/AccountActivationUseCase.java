package com.github.nramc.dev.journey.api.core.usecase.registration;

import com.github.nramc.dev.journey.api.config.ApplicationProperties;
import com.github.nramc.dev.journey.api.core.model.AppUser;
import com.github.nramc.dev.journey.api.core.model.EmailToken;
import com.github.nramc.dev.journey.api.core.services.EmailTokenService;
import com.github.nramc.dev.journey.api.gateway.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
public class AccountActivationUseCase {
    private final ApplicationProperties applicationProperties;
    private final EmailTokenService emailTokenService;
    private final MailService mailService;

    public void sendActivationEmail(AppUser user) {
        EmailToken emailToken = emailTokenService.generateEmailToken(user);
        String activationUrl = getActivationUrl(emailToken, user);
        mailService.sendSimpleEmail(user.username(), "Account Activation", activationUrl);
    }

    private String getActivationUrl(EmailToken emailToken, AppUser user) {
        return UriComponentsBuilder.fromHttpUrl(applicationProperties.uiAppUrl())
                .path("/activate")
                .queryParam("identifier", user.username())
                .queryParam("token", emailToken.token())
                .build().toUriString();
    }
}
