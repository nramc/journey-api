package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.config;

import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpCodeVerifier;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpSecretGenerator;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpCodeGenerator;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.totp.TotpTimeStepWindowProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TOTP Implementation based on <a href="https://datatracker.ietf.org/doc/html/rfc6238">RFC-6238</a> guidelines
 */
@Configuration
@EnableConfigurationProperties(TotpProperties.class)
public class TotpConfig {

    @Bean
    public TotpSecretGenerator totpSecretGenerator(TotpProperties properties) {
        return new TotpSecretGenerator(properties);
    }

    @Bean
    public TotpTimeStepWindowProvider totpTimeStepWindowProvider(TotpProperties properties) {
        return new TotpTimeStepWindowProvider(properties);
    }

    @Bean
    public TotpCodeGenerator totpCodeGenerator(TotpProperties properties, TotpTimeStepWindowProvider timeStepWindowProvider) {
        return new TotpCodeGenerator(properties, timeStepWindowProvider);
    }

    @Bean
    public TotpCodeVerifier totpCodeVerifier(
            TotpProperties totpProperties,
            TotpCodeGenerator totpCodeGenerator,
            TotpTimeStepWindowProvider timeStepWindowProvider) {
        return new TotpCodeVerifier(totpProperties, totpCodeGenerator, timeStepWindowProvider);
    }

}
