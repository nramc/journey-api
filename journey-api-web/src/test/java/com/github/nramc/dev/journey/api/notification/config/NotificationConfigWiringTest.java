package com.github.nramc.dev.journey.api.notification.config;

import com.github.nramc.dev.journey.api.notification.NotificationEventDispatcher;
import com.github.nramc.dev.journey.api.notification.NotificationEventHandler;
import com.github.nramc.dev.journey.api.notification.NotificationEventProcessor;
import com.github.nramc.dev.journey.api.notification.NotificationService;
import com.github.nramc.dev.journey.api.notification.mail.EmailNotificationService;
import com.github.nramc.dev.journey.api.notification.mail.MailSender;
import com.github.nramc.dev.journey.api.notification.processor.AccountActivatedNotificationProcessor;
import com.github.nramc.dev.journey.api.notification.processor.AccountActivationEmailRequestedNotificationProcessor;
import com.github.nramc.dev.journey.api.notification.processor.EmailCodeRequestedNotificationProcessor;
import com.github.nramc.dev.journey.api.notification.processor.JourneyAnniversaryNotificationProcessor;
import com.github.nramc.dev.journey.api.notification.processor.PasswordRecoveryRequestedNotificationProcessor;
import com.github.nramc.dev.journey.api.notification.processor.UserRegisteredNotificationProcessor;
import com.github.nramc.dev.journey.api.notification.telegram.TelegramGateway;
import com.github.nramc.dev.journey.api.notification.telegram.TelegramNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class NotificationConfigWiringTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(NotificationConfig.class, TestSupportConfig.class)
            .withPropertyValues(
                    "app.name=journey",
                    "app.version=1.0.0",
                    "app.ui-app-url=https://journey.codewithram.dev",
                    "service.telegram.base-url=https://api.telegram.org/bot"
            );

    @Test
    void shouldWireCoreNotificationBeansWhenTelegramDisabled() {
        contextRunner
                .withPropertyValues("service.telegram.enabled=false")
                .run(context -> {
                    assertThat(context)
                            .hasSingleBean(MailSender.class)
                            .hasSingleBean(EmailNotificationService.class)
                            .hasSingleBean(NotificationEventDispatcher.class)
                            .hasSingleBean(NotificationEventHandler.class)
                            .doesNotHaveBean(TelegramGateway.class)
                            .doesNotHaveBean(TelegramNotificationService.class)
                            .hasSingleBean(UserRegisteredNotificationProcessor.class)
                            .hasSingleBean(AccountActivationEmailRequestedNotificationProcessor.class)
                            .hasSingleBean(AccountActivatedNotificationProcessor.class)
                            .hasSingleBean(EmailCodeRequestedNotificationProcessor.class)
                            .hasSingleBean(JourneyAnniversaryNotificationProcessor.class)
                            .hasSingleBean(PasswordRecoveryRequestedNotificationProcessor.class);

                    assertThat(context.getBeansOfType(NotificationEventProcessor.class)).hasSize(6);
                    assertThat(context.getBeansOfType(NotificationService.class)).hasSize(1);
                });
    }

    @Test
    void shouldAlsoWireTelegramBeansWhenTelegramEnabled() {
        contextRunner
                .withPropertyValues(
                        "service.telegram.enabled=true",
                        "service.telegram.bot-token=test-token",
                        "service.telegram.channel-id=@journey_channel"
                )
                .run(context -> {
                    assertThat(context)
                            .hasSingleBean(TelegramGateway.class)
                            .hasSingleBean(TelegramNotificationService.class);
                    assertThat(context.getBeansOfType(NotificationService.class)).hasSize(2);
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class TestSupportConfig {

        @Bean
        JavaMailSender javaMailSender() {
            return mock(JavaMailSender.class);
        }

        @Bean
        RestClient.Builder restClientBuilder() {
            return RestClient.builder();
        }
    }
}
