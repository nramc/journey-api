package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.core.app.ApplicationProperties;
import com.github.nramc.dev.journey.api.core.services.mail.MailService;
import com.github.nramc.dev.journey.api.core.services.user.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.core.usecase.codes.token.EmailTokenUseCase;
import com.github.nramc.dev.journey.api.core.usecase.notification.EmailNotificationUseCase;
import com.github.nramc.dev.journey.api.core.usecase.registration.AccountActivationUseCase;
import com.github.nramc.dev.journey.api.core.usecase.registration.RegistrationUseCase;
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
            ApplicationProperties properties,
            EmailTokenUseCase emailTokenUseCase,
            MailService mailService,
            AuthUserDetailsService userDetailsService,
            EmailNotificationUseCase emailNotificationUseCase) {
        return new AccountActivationUseCase(properties, emailTokenUseCase, mailService, userDetailsService, emailNotificationUseCase);
    }

    @Bean
    public RegistrationUseCase registrationUseCase(
            UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, Validator validator,
            AccountActivationUseCase accountActivationUseCase, EmailNotificationUseCase emailNotificationUseCase) {
        return new RegistrationUseCase(userDetailsManager, passwordEncoder, validator, accountActivationUseCase, emailNotificationUseCase);
    }
}
