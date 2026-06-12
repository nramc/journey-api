package com.github.nramc.dev.journey.api.tests.application;

import com.github.nramc.dev.journey.api.JourneyApiApplication;
import com.github.nramc.dev.journey.api.tests.application.config.IntegrationApplicationConfig;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.modulith.moments.support.TimeMachine;

import java.time.Duration;

public class JourneyIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.from(JourneyApiApplication::main)
                .with(IntegrationApplicationConfig.class)
                .run(args);
    }

    @Bean
    public ApplicationRunner init(TimeMachine timeMachine) {
        return args -> timeMachine.shiftBy(Duration.ofDays(1));

    }

}
