package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.web.resources.rest.update.validator.JourneyValidator;
import jakarta.validation.Validator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ApplicationProperties.class})
public class ApplicationContextConfig {

    @Bean
    public JourneyValidator journeyValidator(Validator validator) {
        return new JourneyValidator(validator);
    }

}
