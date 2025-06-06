package com.github.nramc.dev.journey.api.tests.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@TestConfiguration(proxyBeanMethods = false)
@EnableConfigurationProperties({EnvironmentProperties.class})
@PropertySource("classpath:qa-test.properties")
@Profile("qa-test")
public class QaTestSuiteConfig {

}
