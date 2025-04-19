package com.github.nramc.dev.journey.api.tests.application.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration(proxyBeanMethods = false)
@Import({TestUserSetupConfig.class})
public class IntegrationApplicationConfig {

}
