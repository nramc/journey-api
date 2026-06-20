package com.github.nramc.dev.journey.api.tts.client;

import com.github.nramc.dev.journey.api.tts.config.TtsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.web.client.RestClient;

import java.util.Objects;

/**
 * HTTP client for communicating with the Piper TTS server.
 * The Piper server exposes a simple HTTP API for speech synthesis.
 */
@Slf4j
public class PiperHttpClient {

    private final RestClient restClient;
    private final TtsProperties properties;

    public PiperHttpClient(TtsProperties properties, RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl())
                .build();
        this.properties = properties;
    }

    /**
     * Synthesizes speech from text using the Piper server.
     *
     * @param text        the text to convert to speech
     * @param voice       the voice model to use
     * @param lengthScale length scale parameter (affects speed)
     * @param noiseScale  noise scale parameter
     * @param noiseWScale noise w scale parameter
     * @return the synthesized audio as byte array
     */
    @Retryable(value = Exception.class, maxRetries = 3)
    public byte[] synthesize(String text, String voice, Double lengthScale, Double noiseScale, Double noiseWScale) {
        log.debug("Synthesizing speech: textLength={}, voice={}", text.length(), voice);

        var audio = restClient.post()
                .uri("/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .body(createRequestBody(text, voice, lengthScale, noiseScale, noiseWScale))
                .retrieve()
                .toEntity(byte[].class)
                .getBody();

        log.debug("Speech synthesized successfully, audio size: {} bytes", Objects.requireNonNull(audio).length);
        return audio;
    }

    /**
     * Creates the request body for the Piper server.
     * Piper HTTP server expects a JSON with text, voice, and optional parameters.
     */
    private PiperSynthesizeRequest createRequestBody(String text, String voice,
                                                     Double lengthScale, Double noiseScale, Double noiseWScale) {
        return new PiperSynthesizeRequest(
                text,
                resolveVoice(voice),
                resolveLengthScale(lengthScale),
                resolveNoiseScale(noiseScale),
                resolveNoiseWScale(noiseWScale)
        );
    }

    private String resolveVoice(String voice) {
        return voice != null ? voice : properties.defaultVoice();
    }

    private double resolveLengthScale(Double lengthScale) {
        return lengthScale != null ? lengthScale : properties.defaultLengthScale();
    }

    private double resolveNoiseScale(Double noiseScale) {
        return noiseScale != null ? noiseScale : properties.defaultNoiseScale();
    }

    private double resolveNoiseWScale(Double noiseWScale) {
        return noiseWScale != null ? noiseWScale : properties.defaultNoiseWScale();
    }

    /**
     * Request body for Piper HTTP server.
     */
    private record PiperSynthesizeRequest(
            String text,
            String voice,
            double length_scale,
            double noise_scale,
            double noise_w_scale
    ) {
    }
}
