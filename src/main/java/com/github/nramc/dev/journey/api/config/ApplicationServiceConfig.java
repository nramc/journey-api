package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.core.services.mail.MailService;
import com.github.nramc.dev.journey.api.core.usecase.codes.ConfirmationCodeVerifier;
import com.github.nramc.dev.journey.api.core.usecase.codes.emailcode.EmailCodeUseCase;
import com.github.nramc.dev.journey.api.core.usecase.codes.emailcode.EmailCodeValidator;
import com.github.nramc.dev.journey.api.core.usecase.codes.token.EmailTokenUseCase;
import com.github.nramc.dev.journey.api.core.usecase.codes.totp.QRCodeGenerator;
import com.github.nramc.dev.journey.api.core.usecase.codes.totp.TotpCodeVerifier;
import com.github.nramc.dev.journey.api.core.usecase.codes.totp.TotpProperties;
import com.github.nramc.dev.journey.api.core.usecase.codes.totp.TotpSecretGenerator;
import com.github.nramc.dev.journey.api.core.usecase.codes.totp.TotpUseCase;
import com.github.nramc.dev.journey.api.repository.user.attributes.UserSecurityAttributeService;
import com.github.nramc.dev.journey.api.repository.user.attributes.UserSecurityAttributesRepository;
import com.github.nramc.dev.journey.api.repository.user.code.ConfirmationCodeRepository;
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
    public EmailCodeValidator emailCodeValidator(ConfirmationCodeRepository codeRepository) {
        return new EmailCodeValidator(codeRepository);
    }

    @Bean
    public EmailCodeUseCase emailCodeService(
            MailService mailService,
            ConfirmationCodeRepository codeRepository,
            EmailCodeValidator codeValidator) {

        return new EmailCodeUseCase(mailService, codeRepository, codeValidator);
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
            UserSecurityAttributeService userSecurityAttributeService) {
        return new TotpUseCase(properties, secretGenerator, qrCodeGenerator, codeVerifier, userSecurityAttributeService);
    }

    @Bean
    public ConfirmationCodeVerifier confirmationCodeVerifier(
            TotpUseCase totpUseCase, EmailCodeUseCase emailCodeUseCase) {
        return new ConfirmationCodeVerifier(totpUseCase, emailCodeUseCase);
    }

}
