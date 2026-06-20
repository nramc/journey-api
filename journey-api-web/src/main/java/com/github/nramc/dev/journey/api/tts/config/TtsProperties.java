package com.github.nramc.dev.journey.api.tts.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the Text-to-Speech module.
 * Configures the connection to the Piper TTS server.
 */
@Validated
@ConfigurationProperties(prefix = "journey.module.tts")
@Builder(toBuilder = true)
public record TtsProperties(
        @NotBlank String baseUrl,
        @NotBlank String defaultVoice,
        double defaultLengthScale,
        double defaultNoiseScale,
        double defaultNoiseWScale
) {

}
