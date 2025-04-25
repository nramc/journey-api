package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.core.app.health.CloudinaryHealthIndicator;
import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
public class ApplicationActuatorConfig {

    @Bean
    @Profile("!prod")
    public CloudinaryHealthIndicator cloudinaryHealthIndicator(CloudinaryGateway cloudinaryGateway) {
        return new CloudinaryHealthIndicator(cloudinaryGateway);
    }
}
