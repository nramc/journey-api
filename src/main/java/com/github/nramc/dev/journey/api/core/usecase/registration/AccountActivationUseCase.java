package com.github.nramc.dev.journey.api.core.usecase.registration;

import com.github.nramc.dev.journey.api.core.model.AppUser;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AccountActivationUseCase {
    private final MailService mailService;

    public void sendActivationEmail(AppUser user) {
        mailService.sendSimpleEmail(user.username(), "Account Activation", "This is for testing email activation");
    }
}
