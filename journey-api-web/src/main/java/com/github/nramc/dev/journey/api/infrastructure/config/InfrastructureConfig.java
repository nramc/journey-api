package com.github.nramc.dev.journey.api.infrastructure.config;

import com.github.nramc.dev.journey.api.infrastructure.actuator.ApplicationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Root infrastructure configuration.
 *
 * <p>Registers {@link ApplicationProperties} and enables asynchronous execution
 * (required by {@link org.springframework.modulith.events.ApplicationModuleListener}
 * which is a composed {@code @Async + @TransactionalEventListener}).
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ApplicationProperties.class)
@EnableAsync
public class InfrastructureConfig {
}
