package com.github.nramc.dev.journey.api.infrastructure.security;

import com.github.nramc.dev.journey.api.infrastructure.ratelimit.RateLimitFilter;
import com.github.nramc.dev.journey.api.infrastructure.ratelimit.RateLimitKeyResolver;
import com.github.nramc.dev.journey.api.infrastructure.ratelimit.RateLimitProperties;
import com.github.nramc.dev.journey.api.infrastructure.ratelimit.RateLimiterService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RateLimitProperties.class)
public class RateLimitConfig {

    @Bean
    public RateLimitKeyResolver rateLimitKeyResolver() {
        return new RateLimitKeyResolver();
    }

    @Bean
    public RateLimiterService rateLimiterService() {
        return new RateLimiterService();
    }

    @Bean
    public RateLimitFilter rateLimitFilter(RateLimiterService rateLimiterService,
                                           RateLimitKeyResolver rateLimitKeyResolver,
                                           JsonMapper jsonMapper,
                                           RateLimitProperties properties) {
        return new RateLimitFilter(rateLimiterService, rateLimitKeyResolver, jsonMapper, properties);
    }
}
