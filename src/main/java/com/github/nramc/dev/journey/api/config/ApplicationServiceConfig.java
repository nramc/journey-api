package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeRepository;
import com.github.nramc.dev.journey.api.services.MailService;
import com.github.nramc.dev.journey.api.services.email.EmailConfirmationCodeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationServiceConfig {

    @Bean
    public EmailConfirmationCodeService emailCodeService(MailService mailService, ConfirmationCodeRepository codeRepository) {
        return new EmailConfirmationCodeService(mailService, codeRepository);
    }

}
