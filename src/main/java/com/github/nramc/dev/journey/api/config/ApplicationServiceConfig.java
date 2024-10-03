package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.core.usecase.codes.emailcode.EmailCodeValidator;
import com.github.nramc.dev.journey.api.repository.user.attributes.UserSecurityAttributeService;
import com.github.nramc.dev.journey.api.repository.user.attributes.UserSecurityAttributesRepository;
import com.github.nramc.dev.journey.api.repository.user.code.ConfirmationCodeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationServiceConfig {

    @Bean
    public UserSecurityAttributeService userSecurityAttributeService(UserSecurityAttributesRepository attributesRepository) {
        return new UserSecurityAttributeService(attributesRepository);
    }

    @Bean
    public EmailCodeValidator emailCodeValidator(ConfirmationCodeRepository codeRepository) {
        return new EmailCodeValidator(codeRepository);
    }

}
