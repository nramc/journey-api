package com.github.nramc.dev.journey.api.tts.web;

import com.github.nramc.dev.journey.api.infrastructure.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.infrastructure.security.RateLimitConfig;
import com.github.nramc.dev.journey.api.infrastructure.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.shared.exceptions.TechnicalException;
import com.github.nramc.dev.journey.api.tts.TtsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(TtsResource.class)
@Import({WebSecurityConfig.class, InMemoryUserDetailsConfig.class, RateLimitConfig.class})
@ActiveProfiles("test")
class TtsResourceTest {
    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private TtsService ttsService;

    private final byte[] mockAudioData = "mock-audio-content".getBytes();

    @Test
    void synthesize_withValidRequest_usingMockMvcMatchers_shouldReturnOk() {
        when(ttsService.synthesize(any(), any(), any(), any(), any()))
                .thenReturn(mockAudioData);

        var requestBuilder = MockMvcRequestBuilders.post("/api/tts/synthesize")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .content("{\"text\":\"Hello World\"}");
        assertThat(mockMvcTester.perform(requestBuilder))
                .hasStatusOk()
                .hasContentType(MediaType.APPLICATION_OCTET_STREAM)
                .hasHeader("Content-Disposition", "attachment; filename=\"speech.wav\"")
                .body().isEqualTo(mockAudioData);
    }

    @Test
    void synthesize_withAllParameters_shouldPassAllToService() {
        // Given
        String text = "Test text with voice";
        String voice = "en_US-amy-medium";
        Double lengthScale = 1.5;
        Double noiseScale = 0.5;
        Double noiseWScale = 0.6;

        when(ttsService.synthesize(text, voice, lengthScale, noiseScale, noiseWScale)).thenReturn(mockAudioData);

        // When & Then
        String requestBody = """
                {"text":"%s","voice":"%s","lengthScale":%s,"noiseScale":%s,"noiseWScale":%s}"""
                .formatted(text, voice, lengthScale, noiseScale, noiseWScale);

        var requestBuilder = MockMvcRequestBuilders.post("/api/tts/synthesize")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .content(requestBody);

        assertThat(mockMvcTester.perform(requestBuilder))
                .hasStatusOk()
                .hasContentType(MediaType.APPLICATION_OCTET_STREAM)
                .hasHeader("Content-Disposition", "attachment; filename=\"speech.wav\"")
                .body().isEqualTo(mockAudioData);
    }

    @Test
    void synthesize_withOnlyText_shouldUseDefaultParameters() {
        // Given
        when(ttsService.synthesize("Simple text", null, null, null, null)).thenReturn(mockAudioData);

        // When & Then
        var requestBuilder = MockMvcRequestBuilders.post("/api/tts/synthesize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"text":"Simple text"}""");

        assertThat(mockMvcTester.perform(requestBuilder))
                .hasStatusOk()
                .hasContentType(MediaType.APPLICATION_OCTET_STREAM)
                .hasHeader("Content-Disposition", "attachment; filename=\"speech.wav\"")
                .body().isEqualTo(mockAudioData);
    }

    // ==================== VALIDATION CASES ====================

    @Test
    void synthesize_withMissingText_shouldReturnBadRequest() {
        // Given - text is required but missing
        String invalidRequest = """
                {"voice":"en_US-lessac-medium"}""";

        // When & Then
        var requestBuilder = MockMvcRequestBuilders.post("/api/tts/synthesize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest);

        assertThat(mockMvcTester.perform(requestBuilder)).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void synthesize_withEmptyText_usingAssertJ_shouldReturnBadRequest() {
        // When
        var requestBuilder = MockMvcRequestBuilders.post("/api/tts/synthesize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"text":""}""");

        assertThat(mockMvcTester.perform(requestBuilder)).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void synthesize_withBlankText_shouldReturnBadRequest() {
        // When & Then
        var requestBuilder = MockMvcRequestBuilders.post("/api/tts/synthesize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"text":"   "}""");

        assertThat(mockMvcTester.perform(requestBuilder)).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void synthesize_withTextExceedingMaxLength_shouldReturnBadRequest() {
        // Given - text exceeding @Size(max = 10000) constraint
        String longText = "a".repeat(10001);
        String invalidRequest = "{\"text\":\"" + longText + "\"}";

        // When & Then
        var requestBuilder = MockMvcRequestBuilders.post("/api/tts/synthesize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest);

        assertThat(mockMvcTester.perform(requestBuilder)).hasStatus(HttpStatus.BAD_REQUEST);
    }

    // ==================== ERROR CASES ====================

    @Test
    void synthesize_withServiceError_shouldReturn500() {
        // Given
        when(ttsService.synthesize(any(), any(), any(), any(), any()))
                .thenThrow(new TechnicalException("TTS service unavailable", new RuntimeException("TTS service unavailable")));

        // When
        var requestBuilder = MockMvcRequestBuilders.post("/api/tts/synthesize")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"text":"Hello"}""");

        assertThat(mockMvcTester.perform(requestBuilder))
                .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ==================== HEADER VALIDATION ====================

    @Test
    void synthesize_shouldSetCorrectResponseHeaders() {
        // Given
        when(ttsService.synthesize(any(), any(), any(), any(), any())).thenReturn(mockAudioData);

        // When
        var requestBuilder = MockMvcRequestBuilders.post("/api/tts/synthesize")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .content("""
                        {"text":"Test"}""");

        assertThat(mockMvcTester.perform(requestBuilder))
                .hasStatusOk()
                .hasContentType(MediaType.APPLICATION_OCTET_STREAM)
                .hasHeader("Content-Disposition", "attachment; filename=\"speech.wav\"");
    }

}
