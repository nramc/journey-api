package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.core.services.EmailTokenService;
import com.github.nramc.dev.journey.api.core.usecase.notification.EmailNotificationUseCase;
import com.github.nramc.dev.journey.api.core.usecase.registration.AccountActivationUseCase;
import com.github.nramc.dev.journey.api.core.usecase.registration.RegistrationUseCase;
import com.github.nramc.dev.journey.api.gateway.MailService;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.UserSecurityEmailAddressAttributeService;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class ApplicationUseCaseConfig {

    @Bean
    public EmailNotificationUseCase emailNotificationUseCase(MailService mailService, AuthUserDetailsService userDetailsService) {
        return new EmailNotificationUseCase(mailService, userDetailsService);
    }

    @Bean
    public AccountActivationUseCase accountActivationUseCase(
            ApplicationProperties properties, EmailTokenService emailTokenService, MailService mailService,
            AuthUserDetailsService userDetailsService, UserSecurityEmailAddressAttributeService emailAddressAttributeService,
            EmailNotificationUseCase emailNotificationUseCase) {
        return new AccountActivationUseCase(properties, emailTokenService, mailService, userDetailsService,
                emailAddressAttributeService, emailNotificationUseCase);
    }

    @Bean
    public RegistrationUseCase registrationUseCase(
            UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, Validator validator,
            AccountActivationUseCase accountActivationUseCase, EmailNotificationUseCase emailNotificationUseCase) {
        return new RegistrationUseCase(userDetailsManager, passwordEncoder, validator, accountActivationUseCase, emailNotificationUseCase);
    }
}
