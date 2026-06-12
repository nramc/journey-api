package com.github.nramc.dev.journey.api.journey.config;

import com.cloudinary.Cloudinary;
import com.github.nramc.dev.journey.api.journey.gateway.cloudinary.CloudinaryGateway;
import com.github.nramc.dev.journey.api.journey.gateway.cloudinary.CloudinaryProperties;
import com.github.nramc.dev.journey.api.journey.health.CloudinaryHealthIndicator;
import com.github.nramc.dev.journey.api.journey.event.handler.DayHasPassedEventHandler;
import com.github.nramc.dev.journey.api.journey.repository.JourneyService;
import com.github.nramc.dev.journey.api.shared.provider.ActiveUserProvider;
import com.github.nramc.dev.journey.api.journey.web.journeys.update.validator.JourneyValidator;
import jakarta.validation.Validator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Bean declarations for the {@code journey} module.
 *
 * <p>Wires Cloudinary integration, {@link JourneyService}, {@link JourneyValidator},
 * and the Cloudinary health indicator.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CloudinaryProperties.class)
public class JourneyConfig {

    @Bean
    Cloudinary cloudinary(CloudinaryProperties properties) {
        Map<String, String> map = new HashMap<>();
        map.put("cloud_name", properties.cloudName());
        map.put("api_key", properties.apiKey());
        map.put("api_secret", properties.apiSecret());
        map.putAll(properties.additionalProperties());
        return new Cloudinary(map);
    }

    @Bean
    public CloudinaryGateway cloudinaryGateway(Cloudinary cloudinary,
                                               CloudinaryProperties cloudinaryProperties) {
        return new CloudinaryGateway(cloudinary, cloudinaryProperties);
    }

    @Bean
    @Profile("!prod")
    public CloudinaryHealthIndicator cloudinaryHealthIndicator(CloudinaryGateway cloudinaryGateway) {
        return new CloudinaryHealthIndicator(cloudinaryGateway);
    }

    @Bean
    public JourneyService journeyService(MongoTemplate mongoTemplate) {
        return new JourneyService(mongoTemplate);
    }

    @Bean
    public JourneyValidator journeyValidator(Validator validator) {
        return new JourneyValidator(validator);
    }

    @Bean
    public DayHasPassedEventHandler dayHasPassedEventHandler(
            JourneyService journeyService,
            ActiveUserProvider activeUserProvider,
            ApplicationEventPublisher applicationEvents) {
        return new DayHasPassedEventHandler(journeyService, activeUserProvider, applicationEvents);
    }
}
