package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.core.usecase.code.EmailTokenUseCase;
import com.github.nramc.dev.journey.api.core.services.mail.MailService;
import com.github.nramc.dev.journey.api.repository.user.UserSecurityAttributesRepository;
import com.github.nramc.dev.journey.api.repository.user.ConfirmationCodeRepository;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.UserSecurityAttributeService;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.UserSecurityEmailAddressAttributeService;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.code.EmailCodeValidator;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email.code.EmailConfirmationCodeService;
import com.github.nramc.dev.journey.api.core.totp.QRCodeGenerator;
import com.github.nramc.dev.journey.api.core.totp.TotpCodeVerifier;
import com.github.nramc.dev.journey.api.core.totp.TotpSecretGenerator;
import com.github.nramc.dev.journey.api.core.totp.TotpUseCase;
import com.github.nramc.dev.journey.api.core.totp.TotpProperties;
import com.github.nramc.dev.journey.api.core.usecase.code.ConfirmationCodeVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationServiceConfig {

    @Bean
    public EmailTokenUseCase emailTokenService(ConfirmationCodeRepository codeRepository) {
        return new EmailTokenUseCase(codeRepository);
    }


    @Bean
    public UserSecurityAttributeService userSecurityAttributeService(UserSecurityAttributesRepository attributesRepository) {
        return new UserSecurityAttributeService(attributesRepository);
    }

    @Bean
    public UserSecurityEmailAddressAttributeService userSecurityAttributesProvider(
            UserSecurityAttributesRepository userSecurityAttributesRepository) {
        return new UserSecurityEmailAddressAttributeService(userSecurityAttributesRepository);
    }

    @Bean
    public EmailCodeValidator emailCodeValidator(
            ConfirmationCodeRepository codeRepository,
            UserSecurityEmailAddressAttributeService emailAddressAttributeService) {
        return new EmailCodeValidator(codeRepository, emailAddressAttributeService);
    }

    @Bean
    public EmailConfirmationCodeService emailCodeService(
            MailService mailService,
            ConfirmationCodeRepository codeRepository,
            EmailCodeValidator codeValidator,
            UserSecurityEmailAddressAttributeService emailAddressAttributeService) {

        return new EmailConfirmationCodeService(mailService, codeRepository, codeValidator, emailAddressAttributeService);
    }

    @Bean
    public QRCodeGenerator qrCodeGenerator() {
        return new QRCodeGenerator();
    }

    @Bean
    public TotpUseCase totpService(
            TotpProperties properties,
            TotpSecretGenerator secretGenerator,
            QRCodeGenerator qrCodeGenerator,
            TotpCodeVerifier codeVerifier,
            UserSecurityAttributesRepository attributeRepository) {
        return new TotpUseCase(properties, secretGenerator, qrCodeGenerator, codeVerifier, attributeRepository);
    }

    @Bean
    public ConfirmationCodeVerifier confirmationCodeVerifier(
            TotpUseCase totpUseCase, EmailConfirmationCodeService emailConfirmationCodeService) {
        return new ConfirmationCodeVerifier(totpUseCase, emailConfirmationCodeService);
    }

}
