package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.repository.auth.UserSecurityAttributesRepository;
import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeRepository;
import com.github.nramc.dev.journey.api.services.MailService;
import com.github.nramc.dev.journey.api.services.email.EmailCodeValidator;
import com.github.nramc.dev.journey.api.services.email.EmailConfirmationCodeService;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.email.UserSecurityEmailAddressAttributeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationServiceConfig {

    @Bean
    public UserSecurityEmailAddressAttributeService userSecurityAttributesProvider(
            UserSecurityAttributesRepository userSecurityAttributesRepository) {
        return new UserSecurityEmailAddressAttributeService(userSecurityAttributesRepository);
    }

    @Bean
    public EmailCodeValidator emailCodeValidator(ConfirmationCodeRepository codeRepository) {
        return new EmailCodeValidator(codeRepository);
    }

    @Bean
    public EmailConfirmationCodeService emailCodeService(
            MailService mailService,
            ConfirmationCodeRepository codeRepository,
            EmailCodeValidator codeValidator,
            UserSecurityEmailAddressAttributeService emailAddressAttributeService) {

        return new EmailConfirmationCodeService(mailService, codeRepository, codeValidator, emailAddressAttributeService);
    }

}
