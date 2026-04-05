package com.github.nramc.dev.journey.api.config.telegram;

import com.github.nramc.dev.journey.api.core.usecase.notification.TelegramNotificationService;
import com.github.nramc.dev.journey.api.gateway.telegram.TelegramGateway;
import com.github.nramc.dev.journey.api.gateway.telegram.TelegramProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Spring configuration for Telegram Bot notifications.
 *
 * <p>This entire configuration is only activated when
 * {@code service.telegram.enabled=true} is set. When disabled (the default),
 * no beans are created and no Telegram credentials are required.
 *
 * <p>Required environment variables when enabled:
 * <ul>
 *   <li>{@code TELEGRAM_BOT_TOKEN} – token from @BotFather</li>
 *   <li>{@code TELEGRAM_CHANNEL_ID} – channel username (e.g. {@code @mychannel})
 *       or numeric ID (e.g. {@code -1001234567890})</li>
 * </ul>
 */
@Configuration
@ConditionalOnProperty(name = "service.telegram.enabled", havingValue = "true")
@EnableConfigurationProperties(TelegramProperties.class)
public class TelegramConfig {

    @Bean
    public TelegramGateway telegramGateway(TelegramProperties properties, RestClient.Builder restClientBuilder) {
        return new TelegramGateway(properties, restClientBuilder);
    }

    @Bean
    public TelegramNotificationService telegramNotificationService(TelegramGateway telegramGateway) {
        return new TelegramNotificationService(telegramGateway);
    }
}

