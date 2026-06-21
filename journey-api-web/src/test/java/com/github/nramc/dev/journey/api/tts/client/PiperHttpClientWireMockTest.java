package com.github.nramc.dev.journey.api.tts.client;

import com.github.nramc.dev.journey.api.tts.config.TtsProperties;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for {@link PiperHttpClient} using WireMock for HTTP server stubbing.
 *
 * <p>This test class provides real HTTP integration testing by starting an embedded WireMock server
 * that simulates the Piper TTS server API. Unlike Mockito-based tests, these tests exercise the
 * full HTTP client stack including RestClient.
 */
@WireMockTest
class PiperHttpClientWireMockTest {
    private static final TtsProperties PROPERTIES = TtsProperties.builder()
            .baseUrl("http://localhost:5001")
            .defaultVoice("en_US-lessac-medium")
            .defaultLengthScale(1.0)
            .defaultNoiseScale(0.7)
            .defaultNoiseWScale(0.8)
            .build();

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private PiperHttpClient piperHttpClient;
    private final byte[] mockAudioData = "mock-audio-content".getBytes(StandardCharsets.UTF_8);

    @BeforeEach
    void setUp(WireMockRuntimeInfo wireMockRuntimeInfo) {
        WireMock.reset();

        String baseUrl = "http://localhost:" + wireMockRuntimeInfo.getHttpPort();
        var ttsProperties = PROPERTIES.toBuilder().baseUrl(baseUrl).build();
        piperHttpClient = new PiperHttpClient(ttsProperties);
    }

    @AfterEach
    void afterEach() {
        WireMock.reset();
    }

    // ==================== SUCCESS SCENARIOS ====================

    @Test
    void synthesize_withDefaultParameters_shouldReturnAudioData() {
        stubForSuccessResponse(mockAudioData);

        byte[] result = piperHttpClient.synthesize("Hello World", null, null, null, null);

        assertThat(result).as("Audio data from Piper server").isEqualTo(mockAudioData);

        verify(postRequestedFor(urlEqualTo("/"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader("Accept", equalTo(MediaType.APPLICATION_OCTET_STREAM_VALUE)));
    }

    @Test
    void synthesize_withAllParameters_shouldSendCompleteRequestBody() {
        stubForSuccessResponse(mockAudioData);

        String text = "Test text with all parameters";
        String voice = "en_US-amy-medium";
        Double lengthScale = 1.5;
        Double noiseScale = 0.5;
        Double noiseWScale = 0.6;

        byte[] result = piperHttpClient.synthesize(text, voice, lengthScale, noiseScale, noiseWScale);

        assertThat(result).isEqualTo(mockAudioData);

        String expectedBody =
                "{\"text\":\"Test text with all parameters\",\"voice\":\"en_US-amy-medium\",\"length_scale\":1.5,\"noise_scale\":0.5,\"noise_w_scale\":0.6}";
        verify(postRequestedFor(urlEqualTo("/")).withRequestBody(equalToJson(expectedBody)));
    }

    private static Stream<Arguments> textInputsWithDefaultParams() {
        return Stream.of(
                Arguments.of("Partial test",
                        "{\"text\":\"Partial test\",\"voice\":\"en_US-lessac-medium\",\"length_scale\":1.0,\"noise_scale\":0.7,\"noise_w_scale\":0.8}"),
                Arguments.of("",
                        "{\"text\":\"\",\"voice\":\"en_US-lessac-medium\",\"length_scale\":1.0,\"noise_scale\":0.7,\"noise_w_scale\":0.8}"),
                Arguments.of("Hello 世界! 🌍",
                        "{\"text\":\"Hello 世界! 🌍\",\"voice\":\"en_US-lessac-medium\",\"length_scale\":1.0,\"noise_scale\":0.7,\"noise_w_scale\":0.8}")
        );
    }

    @ParameterizedTest(name = "should use default parameters for text: ''{0}''")
    @MethodSource("textInputsWithDefaultParams")
    void synthesize_withNullOptionalParams_shouldUseDefaultsFromProperties(String text, String expectedBody) {
        stubForSuccessResponse(mockAudioData);

        byte[] result = piperHttpClient.synthesize(text, null, null, null, null);

        assertThat(result).isEqualTo(mockAudioData);
        verify(postRequestedFor(urlEqualTo("/")).withRequestBody(equalToJson(expectedBody)));
    }

    @Test
    void synthesize_withVeryLongText_shouldHandleLargeInput() {
        stubForSuccessResponse(mockAudioData);

        String longText = "a".repeat(10000);
        byte[] result = piperHttpClient.synthesize(longText, null, null, null, null);

        assertThat(result).isEqualTo(mockAudioData);
        verify(postRequestedFor(urlEqualTo("/")));
    }

    @Test
    void synthesize_withSpecialCharacters_shouldHandleEscapeSequences() {
        stubForSuccessResponse(mockAudioData);

        String specialText = "Test\nwith\ttabs\rand\nnewlines! @#$%^&*()";
        byte[] result = piperHttpClient.synthesize(specialText, null, null, null, null);

        assertThat(result).isEqualTo(mockAudioData);
        verify(postRequestedFor(urlEqualTo("/")));
    }

    @Test
    void synthesize_withZeroScaleValues_shouldPassValues() {
        stubForSuccessResponse(mockAudioData);

        byte[] result = piperHttpClient.synthesize("Test", null, 0.0, 0.0, 0.0);

        assertThat(result).isEqualTo(mockAudioData);

        String expectedBody = "{\"text\":\"Test\",\"voice\":\"en_US-lessac-medium\",\"length_scale\":0.0,\"noise_scale\":0.0,\"noise_w_scale\":0.0}";
        verify(postRequestedFor(urlEqualTo("/")).withRequestBody(equalToJson(expectedBody)));
    }

    // ==================== MULTIPLE CALLS ====================

    @Test
    void synthesize_shouldBeCallableMultipleTimes() {
        stubForSuccessResponse(mockAudioData);

        byte[] result1 = piperHttpClient.synthesize("First", null, null, null, null);
        byte[] result2 = piperHttpClient.synthesize("Second", null, null, null, null);
        byte[] result3 = piperHttpClient.synthesize("Third", null, null, null, null);

        assertThat(result1).isEqualTo(mockAudioData);
        assertThat(result2).isEqualTo(mockAudioData);
        assertThat(result3).isEqualTo(mockAudioData);

        verify(3, postRequestedFor(urlEqualTo("/")));
    }

    // ==================== ERROR SCENARIOS ====================

    @ParameterizedTest(name = "should throw exception for HTTP {0}")
    @ValueSource(ints = {400, 404, 500, 503})
    void synthesize_whenServerReturnsErrorStatus_shouldThrowException(int statusCode) {
        WireMock.stubFor(post("/")
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Error response\"}")));

        assertThatThrownBy(() -> piperHttpClient.synthesize("Test", null, null, null, null))
                .as("Exception from HTTP %d".formatted(statusCode))
                .isInstanceOf(Exception.class);
    }

    @Test
    void synthesize_whenServerReturnsEmptyBody_shouldThrowException() {
        WireMock.stubFor(post("/")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/octet-stream")
                        .withBody("")));

        assertThatThrownBy(() -> piperHttpClient.synthesize("Test", null, null, null, null))
                .as("Exception from empty response body")
                .isInstanceOf(NullPointerException.class);
    }

    // ==================== HEADER VERIFICATION ====================

    @Test
    void synthesize_shouldSetContentTypeHeader() {
        stubForSuccessResponse(mockAudioData);

        piperHttpClient.synthesize("Test", null, null, null, null);

        verify(postRequestedFor(urlEqualTo("/"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE)));
    }

    @Test
    void synthesize_shouldSetAcceptHeader() {
        stubForSuccessResponse(mockAudioData);

        piperHttpClient.synthesize("Test", null, null, null, null);

        verify(postRequestedFor(urlEqualTo("/"))
                .withHeader("Accept", equalTo(MediaType.APPLICATION_OCTET_STREAM_VALUE)));
    }

    // ==================== REQUEST BODY VERIFICATION ====================

    @Test
    void synthesize_shouldSendRequestBodyForEachCall() {
        stubForSuccessResponse(mockAudioData);

        piperHttpClient.synthesize("Text 1", null, null, null, null);
        piperHttpClient.synthesize("Text 2", null, null, null, null);
        piperHttpClient.synthesize("Text 3", null, null, null, null);

        verify(3, postRequestedFor(urlEqualTo("/")));

        List<ServeEvent> serveEvents = getAllServeEvents();
        assertThat(serveEvents).hasSize(3);
        serveEvents.forEach(event ->
                assertThat(event.getRequest().getBody()).isNotEmpty());
    }

    // ==================== PROPERTIES VERIFICATION ====================

    @Test
    void synthesize_shouldUsePropertiesBaseUrl() {
        stubForSuccessResponse(mockAudioData);

        byte[] result = piperHttpClient.synthesize("Test", null, null, null, null);

        assertThat(result).isEqualTo(mockAudioData);
        verify(postRequestedFor(urlEqualTo("/")));
    }

    @Test
    void synthesize_shouldUseDefaultVoiceFromProperties() {
        stubForSuccessResponse(mockAudioData);

        byte[] result = piperHttpClient.synthesize("Test", null, null, null, null);

        assertThat(result).isEqualTo(mockAudioData);

        String expectedBody = "{\"text\":\"Test\",\"voice\":\"en_US-lessac-medium\",\"length_scale\":1.0,\"noise_scale\":0.7,\"noise_w_scale\":0.8}";
        verify(postRequestedFor(urlEqualTo("/")).withRequestBody(equalToJson(expectedBody)));
    }

    // ==================== HELPER METHODS ====================

    private void stubForSuccessResponse(byte[] responseBody) {
        WireMock.stubFor(post("/")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .withBody(responseBody)));
    }
}
