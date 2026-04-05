package com.github.nramc.dev.journey.api.gateway.telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Gateway for the <a href="https://core.telegram.org/bots/api">Telegram Bot API</a>.
 *
 * <p>Uses Telegram's {@code sendMessage} endpoint to post messages to a configured
 * channel. The bot must be an <b>Administrator</b> of the target channel with the
 * "Post Messages" permission enabled.
 *
 * <p>All errors are caught and logged without re-throwing so that a Telegram outage
 * never interrupts the application's primary flows.
 */
@Slf4j
public class TelegramGateway {

    private static final String TELEGRAM_API_BASE_URL = "https://api.telegram.org/%s";

    private final TelegramProperties properties;
    private final RestClient restClient;

    public TelegramGateway(TelegramProperties properties, RestClient.Builder restClientBuilder) {
        if (properties.botToken() == null || properties.botToken().isBlank()) {
            throw new IllegalStateException(
                    "service.telegram.bot-token must be set when Telegram notifications are enabled");
        }
        if (properties.channelId() == null || properties.channelId().isBlank()) {
            throw new IllegalStateException(
                    "service.telegram.channel-id must be set when Telegram notifications are enabled");
        }
        this.properties = properties;
        this.restClient = restClientBuilder.baseUrl(TELEGRAM_API_BASE_URL.formatted(properties.botToken())).build();
    }

    /**
     * Sends a message to the configured channel using the parse mode from configuration
     * (defaults to {@link TelegramProperties.ParseMode#HTML}).
     *
     * <p>HTML subset supported by Telegram:
     * {@code <b>bold</b>}, {@code <i>italic</i>}, {@code <code>inline code</code>},
     * {@code <pre>preformatted</pre>}, {@code <a href="...">link</a>}.
     *
     * @param text the message text (Telegram limit: 4096 characters)
     */
    public void sendMessage(String text) {
        sendMessage(text, properties.parseMode());
    }

    /**
     * Sends a message to the configured channel with an explicit parse mode.
     *
     * @param text      the message text
     * @param parseMode the formatting mode; use {@link TelegramProperties.ParseMode#NONE} for plain text
     */
    public void sendMessage(String text, TelegramProperties.ParseMode parseMode) {
        try {
            SendMessageRequest request = new SendMessageRequest(
                    properties.channelId(), text, parseMode.mode());

            TelegramApiResponse response = restClient.post()
                    .uri("/sendMessage")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(TelegramApiResponse.class);

            if (response != null && response.ok()) {
                log.info("Telegram message sent successfully");
            } else {
                log.warn("Telegram API returned non-ok response for channel {}: {}",
                        properties.channelId(),
                        response != null ? response.description() : "null response");
            }
        } catch (Exception ex) {
            log.error("Failed to send Telegram notification: {}", ex.getMessage(), ex);
        }
    }

    private record SendMessageRequest(
            @JsonProperty("chat_id") String chatId,
            @JsonProperty("text") String text,
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @JsonProperty("parse_mode") String parseMode) {
    }

    private record TelegramApiResponse(
            boolean ok,
            String description) {
    }
}

