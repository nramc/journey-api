package com.github.nramc.dev.journey.api.security.totp.config;

import com.github.nramc.dev.journey.api.security.totp.TotpCodeGenerator;
import com.github.nramc.dev.journey.api.security.totp.TotpCodeVerifier;
import com.github.nramc.dev.journey.api.security.totp.TotpSecretGenerator;
import com.github.nramc.dev.journey.api.security.totp.TotpTimeStepWindowProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public TotpCodeVerifier totpCodeVerifier(TotpCodeGenerator totpCodeGenerator) {
        return new TotpCodeVerifier(totpCodeGenerator);
    }

}
