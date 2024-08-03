package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.core.usecase.registration.RegistrationUseCase;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class ApplicationUseCaseConfig {

    @Bean
    public RegistrationUseCase registrationUseCase(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, Validator validator) {
        return new RegistrationUseCase(userDetailsManager, passwordEncoder, validator);
    }
}
