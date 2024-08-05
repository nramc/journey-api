package com.github.nramc.dev.journey.api.core.usecase.registration;

import com.github.nramc.dev.journey.api.config.ApplicationProperties;
import com.github.nramc.dev.journey.api.core.model.AppUser;
import com.github.nramc.dev.journey.api.core.model.EmailToken;
import com.github.nramc.dev.journey.api.core.services.EmailTokenService;
import com.github.nramc.dev.journey.api.gateway.MailService;
import com.github.nramc.dev.journey.api.web.exceptions.TechnicalException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class AccountActivationUseCase {
    private static final String EMAIL_TEMPLATE_NAME = "account-activation-template.html";
    private final ApplicationProperties applicationProperties;
    private final EmailTokenService emailTokenService;
    private final MailService mailService;

    public void sendActivationEmail(AppUser user) {
        EmailToken emailToken = emailTokenService.generateEmailToken(user);
        String activationUrl = getActivationUrl(emailToken, user);
        sendActivationEmail(activationUrl, user);
    }

    private void sendActivationEmail(String activationUrl, AppUser user) {
        try {
            Map<String, Object> placeholders = new HashMap<>();
            placeholders.put("name", user.name());
            placeholders.put("activationUrl", activationUrl);
            mailService.sendEmailUsingTemplate(EMAIL_TEMPLATE_NAME, user.username(), "Journey: Activate your account", placeholders);
        } catch (MessagingException ex) {
            throw new TechnicalException("Unable to send activation email", ex);
        }
    }

    private String getActivationUrl(EmailToken emailToken, AppUser user) {
        return UriComponentsBuilder.fromHttpUrl(applicationProperties.uiAppUrl())
                .path("/activate")
                .queryParam("identifier", user.username())
                .queryParam("token", emailToken.token())
                .build().toUriString();
    }
}
