package com.github.nramc.dev.journey.api.core.usecase.registration;

import com.github.nramc.dev.journey.api.core.app.ApplicationProperties;
import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.domain.EmailToken;
import com.github.nramc.dev.journey.api.core.domain.EmailAddress;
import com.github.nramc.dev.journey.api.core.usecase.codes.token.EmailTokenUseCase;
import com.github.nramc.dev.journey.api.core.usecase.notification.EmailNotificationUseCase;
import com.github.nramc.dev.journey.api.core.services.mail.MailService;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.core.exceptions.BusinessException;
import com.github.nramc.dev.journey.api.core.exceptions.TechnicalException;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.UserSecurityEmailAddressAttributeService;
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
    private final EmailTokenUseCase emailTokenUseCase;
    private final MailService mailService;
    private final AuthUserDetailsService userDetailsService;
    private final UserSecurityEmailAddressAttributeService emailAddressAttributeService;
    private final EmailNotificationUseCase emailNotificationUseCase;

    public void sendActivationEmail(AppUser user) {
        EmailToken emailToken = emailTokenUseCase.generateEmailToken(user);
        String activationUrl = getActivationUrl(emailToken, user);
        sendActivationEmail(activationUrl, user);
    }

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

        emailAddressAttributeService.saveSecurityEmailAddress(updatedUserEntity, EmailAddress.valueOf(userEntity.getUsername()));
        emailNotificationUseCase.notifyAdmin("User completed onboarding - " + userEntity.getUsername());
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
                .path("/activation")
                .queryParam("identifier", user.username())
                .queryParam("token", emailToken.token())
                .build().toUriString();
    }
}
