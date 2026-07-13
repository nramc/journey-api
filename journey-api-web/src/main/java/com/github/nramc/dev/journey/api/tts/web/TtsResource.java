package com.github.nramc.dev.journey.api.tts.web;

import com.github.nramc.dev.journey.api.shared.web.Resources;
import com.github.nramc.dev.journey.api.shared.web.doc.RestDocCommonResponse;
import com.github.nramc.dev.journey.api.tts.TtsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Text-to-Speech operations.
 * Provides endpoints for synthesizing speech from text.
 */
@Slf4j
@RestController
@RequestMapping(Resources.TTS_API)
@RequiredArgsConstructor
@Tag(name = "Text-to-Speech", description = "Operations for text-to-speech synthesis")
public class TtsResource {

    private final TtsService ttsService;

    /**
     * Synthesizes speech from text.
     *
     * @param request the TTS request containing text and optional parameters
     * @return the synthesized audio file
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(
            summary = "Synthesize speech from text",
            description = "Converts text to speech using the Piper TTS server. Returns audio in WAV format."
    )
    @ApiResponse(responseCode = "200", description = "Audio file",
            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    schema = @Schema(type = "string", format = "binary")))
    @RestDocCommonResponse
    public ResponseEntity<byte[]> synthesize(@Valid @RequestBody TtsRequest request) {
        log.info("Received TTS request for text of length: {}", request.text().length());

        var audio = ttsService.synthesize(
                request.text(),
                request.voice(),
                request.lengthScale(),
                request.noiseScale(),
                request.noiseWScale()
        );
        log.info("TTS request completed successfully");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"speech.wav\"")
                .body(audio);
    }

}
