package com.github.nramc.dev.journey.api.tests.application.config;

import com.github.nramc.dev.journey.api.journey.gateway.cloudinary.CloudinaryGateway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.modulith.moments.support.TimeMachine;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
@Import({TestUserSetupConfig.class})
public class IntegrationApplicationConfig {

    @Bean
    @Primary
    public CloudinaryGateway cloudinaryGatewayStub() {
        return new CloudinaryGatewayStub(null, null);
    }

    @Bean
    public ApplicationRunner init(TimeMachine timeMachine) {
        return args -> timeMachine.shiftBy(Duration.ofDays(1));
    }

}
