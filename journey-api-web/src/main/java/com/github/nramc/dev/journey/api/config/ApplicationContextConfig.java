package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.core.app.ApplicationProperties;
import com.github.nramc.dev.journey.api.core.jwt.JwtGenerator;
import com.github.nramc.dev.journey.api.core.jwt.JwtProperties;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.provider.JwtResponseProvider;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.validator.JourneyValidator;
import jakarta.validation.Validator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;


@Configuration
@EnableConfigurationProperties({ApplicationProperties.class})
public class ApplicationContextConfig {

    @Bean
    public JourneyValidator journeyValidator(Validator validator) {
        return new JourneyValidator(validator);
    }

    @Bean
    public JwtGenerator jwtGenerator(JwtProperties jwtProperties, JwtEncoder jwtEncoder) {
        return new JwtGenerator(jwtProperties, jwtEncoder);
    }

    @Bean
    JwtResponseProvider jwtResponseProvider(JwtGenerator jwtGenerator) {
        return new JwtResponseProvider(jwtGenerator);
    }


}
