package com.github.nramc.dev.journey.api.gateway.telegram;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.client.RestClient;

import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.notContaining;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest
class TelegramGatewayTest {

    private static final String BOT_TOKEN = "bot123456789:AABBCCDDtest-token";
    private static final String CHANNEL_ID = "@journey_channel";
    private static final String SEND_MESSAGE_PATH = "/" + BOT_TOKEN + "/sendMessage";
    private static final String API_OK_RESPONSE = "{\"ok\":true,\"result\":{\"message_id\":1}}";
    private static final String API_ERROR_RESPONSE =
            "{\"ok\":false,\"description\":\"Bad Request: chat not found\"}";

    private static final String DUMMY_URL = "http://localhost:1";
    private static final RestClient.Builder REST_CLIENT_BUILDER = RestClient.builder();

    private TelegramGateway telegramGateway;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wireMockRuntimeInfo) {
        // Point the gateway at WireMock's random HTTP port via the configurable baseUrl.
        TelegramProperties properties = new TelegramProperties(
                true, BOT_TOKEN, CHANNEL_ID, TelegramProperties.ParseMode.HTML,
                wireMockRuntimeInfo.getHttpBaseUrl());
        telegramGateway = new TelegramGateway(properties, REST_CLIENT_BUILDER);
    }

    @Test
    void constructor_whenBotTokenIsNull_shouldThrowIllegalStateException() {
        TelegramProperties props = new TelegramProperties(
                true, null, CHANNEL_ID, TelegramProperties.ParseMode.HTML, DUMMY_URL);

        assertThatThrownBy(() -> new TelegramGateway(props, REST_CLIENT_BUILDER))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("bot-token");
    }

    @Test
    void constructor_whenBotTokenIsBlank_shouldThrowIllegalStateException() {
        TelegramProperties props = new TelegramProperties(
                true, "   ", CHANNEL_ID, TelegramProperties.ParseMode.HTML, DUMMY_URL);

        assertThatThrownBy(() -> new TelegramGateway(props, REST_CLIENT_BUILDER))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("bot-token");
    }

    @Test
    void constructor_whenChannelIdIsNull_shouldThrowIllegalStateException() {
        TelegramProperties props = new TelegramProperties(
                true, BOT_TOKEN, null, TelegramProperties.ParseMode.HTML, DUMMY_URL);

        assertThatThrownBy(() -> new TelegramGateway(props, REST_CLIENT_BUILDER))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("channel-id");
    }

    @Test
    void constructor_whenChannelIdIsBlank_shouldThrowIllegalStateException() {
        TelegramProperties props = new TelegramProperties(
                true, BOT_TOKEN, "  ", TelegramProperties.ParseMode.HTML, DUMMY_URL);

        assertThatThrownBy(() -> new TelegramGateway(props, REST_CLIENT_BUILDER))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("channel-id");
    }

    // ── Happy-path HTTP interaction ─────────────────────────────────────────

    @Test
    void sendMessage_whenApiReturnsOkTrue_shouldCompleteSuccessfully() {
        stubFor(post(urlEqualTo(SEND_MESSAGE_PATH))
                .willReturn(okJson(API_OK_RESPONSE)));

        assertThatNoException().isThrownBy(() -> telegramGateway.sendMessage("Hello World"));

        verify(postRequestedFor(urlEqualTo(SEND_MESSAGE_PATH)));
    }

    @Test
    void sendMessage_shouldPostToCorrectTelegramEndpoint() {
        stubFor(post(urlEqualTo(SEND_MESSAGE_PATH))
                .willReturn(okJson(API_OK_RESPONSE)));

        telegramGateway.sendMessage("test");

        verify(postRequestedFor(urlEqualTo(SEND_MESSAGE_PATH)));
    }

    @Test
    void sendMessage_shouldSendJsonContentType() {
        stubFor(post(urlEqualTo(SEND_MESSAGE_PATH))
                .willReturn(okJson(API_OK_RESPONSE)));

        telegramGateway.sendMessage("test");

        verify(postRequestedFor(urlEqualTo(SEND_MESSAGE_PATH))
                .withHeader("Content-Type", containing("application/json")));
    }

    @Test
    void sendMessage_shouldSetConfiguredChannelAsChatId() {
        stubFor(post(urlEqualTo(SEND_MESSAGE_PATH))
                .willReturn(okJson(API_OK_RESPONSE)));

        telegramGateway.sendMessage("test");

        verify(postRequestedFor(urlEqualTo(SEND_MESSAGE_PATH))
                .withRequestBody(matchingJsonPath("$.chat_id", equalTo(CHANNEL_ID))));
    }

    @Test
    void sendMessage_shouldIncludeTextInRequestBody() {
        String expectedText = "Journey API notification: user registered";
        stubFor(post(urlEqualTo(SEND_MESSAGE_PATH))
                .willReturn(okJson(API_OK_RESPONSE)));

        telegramGateway.sendMessage(expectedText);

        verify(postRequestedFor(urlEqualTo(SEND_MESSAGE_PATH))
                .withRequestBody(matchingJsonPath("$.text", equalTo(expectedText))));
    }

    @Test
    void sendMessage_withNoArgOverload_shouldUseConfiguredParseModeFromProperties() {
        // setUp() configures properties with ParseMode.HTML
        stubFor(post(urlEqualTo(SEND_MESSAGE_PATH))
                .willReturn(okJson(API_OK_RESPONSE)));

        telegramGateway.sendMessage("<b>Hello</b>");

        verify(postRequestedFor(urlEqualTo(SEND_MESSAGE_PATH))
                .withRequestBody(matchingJsonPath("$.parse_mode", equalTo("HTML"))));
    }

    // ── Parse mode variants ─────────────────────────────────────────────────

    static Stream<Arguments> parseModeApiValues() {
        return Stream.of(
                Arguments.of(TelegramProperties.ParseMode.HTML, "HTML"),
                Arguments.of(TelegramProperties.ParseMode.MARKDOWN_V2, "MarkdownV2")
        );
    }

    @ParameterizedTest(name = "parseMode={0} → Telegram api value \"{1}\"")
    @MethodSource("parseModeApiValues")
    void sendMessage_withExplicitParseMode_shouldSendCorrectApiValue(
            TelegramProperties.ParseMode parseMode, String expectedApiValue) {

        stubFor(post(urlEqualTo(SEND_MESSAGE_PATH))
                .willReturn(okJson(API_OK_RESPONSE)));

        telegramGateway.sendMessage("test", parseMode);

        verify(postRequestedFor(urlEqualTo(SEND_MESSAGE_PATH))
                .withRequestBody(matchingJsonPath("$.parse_mode", equalTo(expectedApiValue))));
    }

    @Test
    void sendMessage_withNoneParseMode_shouldOmitParseModeFieldFromRequest() {
        // ParseMode.NONE → mode() returns "" → @JsonInclude(NON_EMPTY) omits the field entirely.
        // Sending parse_mode="" would be rejected by the Telegram API.
        stubFor(post(urlEqualTo(SEND_MESSAGE_PATH))
                .willReturn(okJson(API_OK_RESPONSE)));

        telegramGateway.sendMessage("plain text message", TelegramProperties.ParseMode.NONE);

        verify(postRequestedFor(urlEqualTo(SEND_MESSAGE_PATH))
                .withRequestBody(notContaining("parse_mode")));
    }

    // ── Error resilience ────────────────────────────────────────────────────

    @Test
    void sendMessage_whenApiReturnsOkFalse_shouldNotThrowException() {
        stubFor(post(urlEqualTo(SEND_MESSAGE_PATH))
                .willReturn(okJson(API_ERROR_RESPONSE)));

        // Gateway swallows non-ok responses and logs a warning — primary flow is unaffected.
        assertThatNoException().isThrownBy(() -> telegramGateway.sendMessage("Hello"));
    }

    @Test
    void sendMessage_whenApiReturnsServerError_shouldNotThrowException() {
        stubFor(post(urlEqualTo(SEND_MESSAGE_PATH))
                .willReturn(serverError()));

        assertThatNoException().isThrownBy(() -> telegramGateway.sendMessage("Hello"));
    }

    @Test
    void sendMessage_whenNetworkFaultOccurs_shouldNotThrowException() {
        stubFor(post(urlEqualTo(SEND_MESSAGE_PATH))
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

        assertThatNoException().isThrownBy(() -> telegramGateway.sendMessage("Hello"));
    }
}

