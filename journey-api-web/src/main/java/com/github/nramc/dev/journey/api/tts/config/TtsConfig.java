package com.github.nramc.dev.journey.api.tts.config;

import com.github.nramc.dev.journey.api.tts.TtsService;
import com.github.nramc.dev.journey.api.tts.client.PiperHttpClient;
import com.github.nramc.dev.journey.api.tts.service.PiperTtsService;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.Optional;

/**
 * Main configuration class for the TTS module.
 * Enables configuration properties and imports WebClient configuration.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(TtsProperties.class)
public class TtsConfig {

    @Bean
    public PiperHttpClient piperHttpClient(TtsProperties properties) {
        return new PiperHttpClient(properties);
    }

    @Bean
    public TtsService ttsService(PiperHttpClient piperHttpClient) {
        return new PiperTtsService(piperHttpClient);
    }

    /**
     * Fixes the OpenAPI schema for the TTS synthesize endpoint.
     * springdoc auto-generates a schema for {@code ResponseEntity<byte[]>} that includes
     * an invalid {@code default} field, which fails ZAP's swagger-parser validation:
     * "schema.default is not of type object". This customizer replaces that schema with
     * a clean {@code type: string, format: binary} definition.
     */
    @Bean
    public OpenApiCustomizer ttsOpenApiCustomizer() {
        return openApi -> Optional.ofNullable(openApi.getPaths())
                .map(paths -> paths.get("/api/tts/synthesize"))
                .map(PathItem::getPost)
                .map(Operation::getResponses)
                .map(responses -> responses.get("200"))
                .map(ApiResponse::getContent)
                .map(content -> content.get(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .ifPresent(mediaType -> {
                    Schema<byte[]> binarySchema = new Schema<>();
                    binarySchema.setType("string");
                    binarySchema.setFormat("binary");
                    binarySchema.setDescription("Audio file in WAV format");
                    mediaType.setSchema(binarySchema);
                });
    }

}
