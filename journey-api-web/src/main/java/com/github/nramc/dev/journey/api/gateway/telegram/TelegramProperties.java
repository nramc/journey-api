package com.github.nramc.dev.journey.api.gateway.telegram;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Telegram Bot notification channel.
 *
 * <p>Example configuration:
 * <pre>
 * service:
 *   telegram:
 *     enabled: true
 *     bot-token: ${TELEGRAM_BOT_TOKEN}
 *     channel-id: ${TELEGRAM_CHANNEL_ID}   # e.g. @mychannel or -1001234567890
 *     parse-mode: HTML                      # HTML | MARKDOWN_V2 | NONE
 * </pre>
 *
 * <p>The bot must be added as an <b>Administrator</b> of the target channel
 * with the "Post Messages" permission before it can send messages.
 */
@ConfigurationProperties("service.telegram")
public record TelegramProperties(
        boolean enabled,
        @NotBlank String botToken,
        @NotBlank String channelId,
        ParseMode parseMode
) {

    public TelegramProperties {
        parseMode = parseMode != null ? parseMode : ParseMode.HTML;
    }

    /**
     * Telegram message formatting modes.
     *
     * <p>The {@link #mode()} maps each constant to the string the Telegram Bot API
     * expects in the {@code parse_mode} field.
     *
     * @see <a href="https://core.telegram.org/bots/api#formatting-options">
     *      Telegram – Formatting Options</a>
     */
    public enum ParseMode {

        /** Telegram HTML subset: {@code <b>}, {@code <i>}, {@code <code>}, {@code <pre>}, {@code <a>}. */
        HTML("HTML"),

        /**
         * Telegram MarkdownV2. Special characters must be escaped with {@code \}.
         * Prefer {@link #HTML} for programmatic messages.
         */
        MARKDOWN_V2("MarkdownV2"),

        /** No formatting — message is treated as plain text. */
        NONE("");

        private final String mode;

        ParseMode(String mode) {
            this.mode = mode;
        }

        /**
         * Returns the exact string the Telegram API expects for the {@code parse_mode} field.
         */
        public String mode() {
            return mode;
        }
    }
}
