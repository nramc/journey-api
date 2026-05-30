package com.github.nramc.dev.journey.api.infrastructure.config;

import com.github.nramc.dev.journey.api.infrastructure.actuator.ApplicationProperties;
import com.github.nramc.dev.journey.api.infrastructure.event.EventRepublisher;
import com.github.nramc.dev.journey.api.infrastructure.event.EventRepublisherProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Root infrastructure configuration.
 *
 * <p>Registers {@link ApplicationProperties} and enables asynchronous execution
 * (required by {@link org.springframework.modulith.events.ApplicationModuleListener}
 * which is a composed {@code @Async + @TransactionalEventListener}).
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ApplicationProperties.class, EventRepublisherProperties.class})
@EnableAsync
@EnableScheduling
public class InfrastructureConfig {
    @Bean
    public EventRepublisher eventRepublisher(IncompleteEventPublications incompleteEventPublications, EventRepublisherProperties properties) {
        return new EventRepublisher(incompleteEventPublications, properties);
    }
}
