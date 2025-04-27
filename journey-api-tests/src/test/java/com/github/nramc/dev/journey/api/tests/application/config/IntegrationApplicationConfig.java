package com.github.nramc.dev.journey.api.tests.application.config;

import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryGateway;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@TestConfiguration(proxyBeanMethods = false)
@Import({TestUserSetupConfig.class})
public class IntegrationApplicationConfig {

    @Bean
    public CloudinaryGateway cloudinaryGateway() {
        return new CloudinaryGatewayStub(null, null);
    }

}
