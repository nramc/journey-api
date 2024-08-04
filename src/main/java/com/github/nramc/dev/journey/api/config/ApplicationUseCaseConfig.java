package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.core.services.EmailTokenService;
import com.github.nramc.dev.journey.api.core.usecase.registration.AccountActivationUseCase;
import com.github.nramc.dev.journey.api.core.usecase.registration.RegistrationUseCase;
import com.github.nramc.dev.journey.api.gateway.MailService;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class ApplicationUseCaseConfig {

    @Bean
    public AccountActivationUseCase accountActivationUseCase(ApplicationProperties properties, EmailTokenService emailTokenService, MailService mailService) {
        return new AccountActivationUseCase(properties, emailTokenService, mailService);
    }

    @Bean
    public RegistrationUseCase registrationUseCase(
            UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, Validator validator,
            AccountActivationUseCase accountActivationUseCase) {
        return new RegistrationUseCase(userDetailsManager, passwordEncoder, validator, accountActivationUseCase);
    }
}
