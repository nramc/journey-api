package com.github.nramc.dev.journey.api.security.totp.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TotpProperties.class)
public class TotpConfig {
}
