package com.github.nramc.dev.journey.api.tts.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for text-to-speech synthesis.
 */
@Schema(description = "Request for text-to-speech synthesis")
public record TtsRequest(
        @NotBlank
        @Size(max = 10000)
        @Schema(description = "Text to convert to speech", example = "Hello, this is a test of the text-to-speech system.")
        String text,

        @Schema(description = "Voice model to use", example = "en_US-lessac-medium", defaultValue = "en_US-lessac-medium")
        String voice,

        @Schema(description = "Length scale for speech (affects speed)", example = "1.0", defaultValue = "1.0")
        Double lengthScale,

        @Schema(description = "Noise scale for speech", example = "0.7", defaultValue = "0.7")
        Double noiseScale,

        @Schema(description = "Noise w scale for speech", example = "0.8", defaultValue = "0.8")
        Double noiseWScale
) {

    /**
     * Creates a TtsRequest with only the required text field.
     * Optional parameters will use server defaults.
     */
    public static TtsRequest of(String text) {
        return new TtsRequest(text, null, null, null, null);
    }

}
