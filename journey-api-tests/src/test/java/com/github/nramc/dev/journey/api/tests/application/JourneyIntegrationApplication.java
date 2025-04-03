package com.github.nramc.dev.journey.api.tests.application;

import com.github.nramc.dev.journey.api.JourneyApiApplication;
import com.github.nramc.dev.journey.api.tests.application.config.TestContainerConfig;
import org.springframework.boot.SpringApplication;

public class JourneyIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.from(JourneyApiApplication::main)
                .with(TestContainerConfig.class)
                .run(args);
    }
}
