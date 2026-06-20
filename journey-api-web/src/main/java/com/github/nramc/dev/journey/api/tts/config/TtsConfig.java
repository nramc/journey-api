package com.github.nramc.dev.journey.api.tts.config;

import com.github.nramc.dev.journey.api.tts.TtsService;
import com.github.nramc.dev.journey.api.tts.client.PiperHttpClient;
import com.github.nramc.dev.journey.api.tts.service.PiperTtsService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Main configuration class for the TTS module.
 * Enables configuration properties and imports WebClient configuration.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(TtsProperties.class)
public class TtsConfig {

    @Bean
    public PiperHttpClient piperHttpClient(TtsProperties properties, RestClient.Builder restClientBuilder) {
        return new PiperHttpClient(properties, restClientBuilder);
    }

    @Bean
    public TtsService ttsService(PiperHttpClient piperHttpClient) {
        return new PiperTtsService(piperHttpClient);
    }


}
