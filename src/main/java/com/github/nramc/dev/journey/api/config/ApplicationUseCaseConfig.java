package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.core.app.ApplicationProperties;
import com.github.nramc.dev.journey.api.core.services.mail.MailService;
import com.github.nramc.dev.journey.api.repository.user.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.core.usecase.codes.ConfirmationCodeUseCase;
import com.github.nramc.dev.journey.api.core.usecase.codes.emailcode.EmailCodeUseCase;
import com.github.nramc.dev.journey.api.core.usecase.codes.emailcode.EmailCodeValidator;
import com.github.nramc.dev.journey.api.core.usecase.codes.token.EmailTokenUseCase;
import com.github.nramc.dev.journey.api.core.usecase.codes.totp.QRCodeGenerator;
import com.github.nramc.dev.journey.api.core.usecase.codes.totp.TotpCodeVerifier;
import com.github.nramc.dev.journey.api.core.usecase.codes.totp.TotpProperties;
import com.github.nramc.dev.journey.api.core.usecase.codes.totp.TotpSecretGenerator;
import com.github.nramc.dev.journey.api.core.usecase.codes.totp.TotpUseCase;
import com.github.nramc.dev.journey.api.core.usecase.notification.EmailNotificationUseCase;
import com.github.nramc.dev.journey.api.core.usecase.registration.AccountActivationUseCase;
import com.github.nramc.dev.journey.api.core.usecase.registration.RegistrationUseCase;
import com.github.nramc.dev.journey.api.repository.user.attributes.UserSecurityAttributeService;
import com.github.nramc.dev.journey.api.repository.user.code.ConfirmationCodeRepository;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class ApplicationUseCaseConfig {

    @Bean
    public RegistrationUseCase registrationUseCase(
            UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, Validator validator,
            AccountActivationUseCase accountActivationUseCase, EmailNotificationUseCase emailNotificationUseCase) {
        return new RegistrationUseCase(userDetailsManager, passwordEncoder, validator, accountActivationUseCase, emailNotificationUseCase);
    }

    @Bean
    public EmailTokenUseCase emailTokenUseCase(ConfirmationCodeRepository codeRepository) {
        return new EmailTokenUseCase(codeRepository);
    }

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
    public TotpUseCase totpUseCase(
            TotpProperties properties,
            TotpSecretGenerator secretGenerator,
            QRCodeGenerator qrCodeGenerator,
            TotpCodeVerifier codeVerifier,
            UserSecurityAttributeService userSecurityAttributeService) {
        return new TotpUseCase(properties, secretGenerator, qrCodeGenerator, codeVerifier, userSecurityAttributeService);
    }

    @Bean
    public EmailCodeUseCase emailCodeUseCase(
            MailService mailService,
            ConfirmationCodeRepository codeRepository,
            EmailCodeValidator codeValidator) {

        return new EmailCodeUseCase(mailService, codeRepository, codeValidator);
    }
    
    @Bean
    public ConfirmationCodeUseCase confirmationCodeUseCase(
            TotpUseCase totpUseCase, EmailCodeUseCase emailCodeUseCase) {
        return new ConfirmationCodeUseCase(totpUseCase, emailCodeUseCase);
    }
}
