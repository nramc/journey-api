package com.github.nramc.dev.journey.api.account.config;

import com.github.nramc.dev.journey.api.account.codes.ConfirmationCodeUseCase;
import com.github.nramc.dev.journey.api.account.codes.emailcode.EmailCodeUseCase;
import com.github.nramc.dev.journey.api.account.codes.emailcode.EmailCodeValidator;
import com.github.nramc.dev.journey.api.account.codes.token.EmailTokenUseCase;
import com.github.nramc.dev.journey.api.account.codes.totp.QRCodeGenerator;
import com.github.nramc.dev.journey.api.account.codes.totp.TotpCodeGenerator;
import com.github.nramc.dev.journey.api.account.codes.totp.TotpCodeVerifier;
import com.github.nramc.dev.journey.api.account.codes.totp.TotpProperties;
import com.github.nramc.dev.journey.api.account.codes.totp.TotpSecretGenerator;
import com.github.nramc.dev.journey.api.account.codes.totp.TotpTimeStepWindowProvider;
import com.github.nramc.dev.journey.api.account.codes.totp.TotpUseCase;
import com.github.nramc.dev.journey.api.account.jwt.JwtGenerator;
import com.github.nramc.dev.journey.api.account.repository.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.account.repository.UserRepository;
import com.github.nramc.dev.journey.api.account.repository.attributes.UserSecurityAttributeService;
import com.github.nramc.dev.journey.api.account.repository.attributes.UserSecurityAttributesRepository;
import com.github.nramc.dev.journey.api.account.repository.code.ConfirmationCodeRepository;
import com.github.nramc.dev.journey.api.account.usecase.AccountActivationUseCase;
import com.github.nramc.dev.journey.api.account.usecase.RegistrationUseCase;
import com.github.nramc.dev.journey.api.account.web.auth.provider.JwtResponseProvider;
import com.github.nramc.dev.journey.api.infrastructure.actuator.ApplicationProperties;
import com.github.nramc.dev.journey.api.infrastructure.security.JwtProperties;
import jakarta.validation.Validator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * Bean declarations for the {@code account} module.
 *
 * <p>Wires: TOTP stack, email codes, registration & activation use cases, JWT
 * generator/response-provider, and the Spring Security user details service.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({TotpProperties.class, ApplicationProperties.class})
public class AccountConfig {

    // ── Spring Security user details ──────────────────────────────────────

    @Bean
    public AuthUserDetailsService authUserDetailsService(UserRepository userRepository) {
        return new AuthUserDetailsService(userRepository);
    }

    // ── Security attribute service ────────────────────────────────────────

    @Bean
    public UserSecurityAttributeService userSecurityAttributeService(
            UserSecurityAttributesRepository attributesRepository) {
        return new UserSecurityAttributeService(attributesRepository);
    }

    // ── TOTP ──────────────────────────────────────────────────────────────

    @Bean
    public TotpSecretGenerator totpSecretGenerator(TotpProperties properties) {
        return new TotpSecretGenerator(properties);
    }

    @Bean
    public TotpTimeStepWindowProvider totpTimeStepWindowProvider(TotpProperties properties) {
        return new TotpTimeStepWindowProvider(properties);
    }

    @Bean
    public TotpCodeGenerator totpCodeGenerator(TotpProperties properties,
                                               TotpTimeStepWindowProvider timeStepWindowProvider) {
        return new TotpCodeGenerator(properties, timeStepWindowProvider);
    }

    @Bean
    public TotpCodeVerifier totpCodeVerifier(TotpProperties totpProperties,
                                             TotpCodeGenerator totpCodeGenerator,
                                             TotpTimeStepWindowProvider timeStepWindowProvider) {
        return new TotpCodeVerifier(totpProperties, totpCodeGenerator, timeStepWindowProvider);
    }

    @Bean
    public QRCodeGenerator qrCodeGenerator() {
        return new QRCodeGenerator();
    }

    @Bean
    public TotpUseCase totpUseCase(TotpProperties properties,
                                   TotpSecretGenerator secretGenerator,
                                   QRCodeGenerator qrCodeGenerator,
                                   TotpCodeVerifier codeVerifier,
                                   UserSecurityAttributeService userSecurityAttributeService) {
        return new TotpUseCase(properties, secretGenerator, qrCodeGenerator, codeVerifier,
                userSecurityAttributeService);
    }

    // ── Email codes ───────────────────────────────────────────────────────

    @Bean
    public EmailCodeValidator emailCodeValidator(ConfirmationCodeRepository codeRepository) {
        return new EmailCodeValidator(codeRepository);
    }

    @Bean
    public EmailTokenUseCase emailTokenUseCase(ConfirmationCodeRepository codeRepository) {
        return new EmailTokenUseCase(codeRepository);
    }

    @Bean
    public EmailCodeUseCase emailCodeUseCase(
            ConfirmationCodeRepository codeRepository,
            EmailCodeValidator codeValidator,
            ApplicationEventPublisher eventPublisher) {
        return new EmailCodeUseCase(codeRepository, codeValidator, eventPublisher);
    }

    @Bean
    public ConfirmationCodeUseCase confirmationCodeUseCase(TotpUseCase totpUseCase,
                                                           EmailCodeUseCase emailCodeUseCase) {
        return new ConfirmationCodeUseCase(totpUseCase, emailCodeUseCase);
    }

    // ── Registration & activation ──────────────────────────────────────────

    @Bean
    public AccountActivationUseCase accountActivationUseCase(
            ApplicationProperties properties,
            EmailTokenUseCase emailTokenUseCase,
            AuthUserDetailsService userDetailsService,
            ApplicationEventPublisher applicationEvents) {
        return new AccountActivationUseCase(properties, emailTokenUseCase, userDetailsService,
                applicationEvents);
    }

    @Bean
    public RegistrationUseCase registrationUseCase(
            UserDetailsManager userDetailsManager,
            PasswordEncoder passwordEncoder,
            Validator validator,
            AccountActivationUseCase accountActivationUseCase,
            ApplicationEventPublisher applicationEvents) {
        return new RegistrationUseCase(userDetailsManager, passwordEncoder, validator,
                accountActivationUseCase, applicationEvents);
    }

    // ── JWT ───────────────────────────────────────────────────────────────

    @Bean
    public JwtGenerator jwtGenerator(JwtProperties jwtProperties, JwtEncoder jwtEncoder) {
        return new JwtGenerator(jwtProperties, jwtEncoder);
    }

    @Bean
    public JwtResponseProvider jwtResponseProvider(JwtGenerator jwtGenerator) {
        return new JwtResponseProvider(jwtGenerator);
    }
}
