package com.github.nramc.dev.journey.api.notification.config;

import com.github.nramc.dev.journey.api.infrastructure.actuator.ApplicationProperties;
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
import com.github.nramc.dev.journey.api.notification.processor.UserRegisteredNotificationProcessor;
import com.github.nramc.dev.journey.api.notification.telegram.TelegramGateway;
import com.github.nramc.dev.journey.api.notification.telegram.TelegramNotificationService;
import com.github.nramc.dev.journey.api.notification.telegram.TelegramProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestClient;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.List;

/**
 * Bean declarations for the {@code notification} module.
 *
 * <p>Wires mail (Thymeleaf template engine, MailService), Telegram gateway,
 * notification service implementations, and the cross-module event handler.
 */
@Configuration(proxyBeanMethods = false)
@Profile("!test")
@EnableConfigurationProperties({TelegramProperties.class, ApplicationProperties.class})
public class NotificationConfig {

    // ── Mail ──────────────────────────────────────────────────────────────

    @Bean("thymeleafEmailTemplateResolver")
    public ITemplateResolver thymeleafEmailTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("mail-templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        return resolver;
    }

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine(
            @Qualifier("thymeleafEmailTemplateResolver") ITemplateResolver thymeleafEmailTemplateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(thymeleafEmailTemplateResolver);
        return engine;
    }

    @Bean
    public MailSender mailService(
            @Value("classpath:/assets/logo.png") Resource logoResource,
            @Value("classpath:/mail-templates/layout/style.css") Resource cssResource,
            JavaMailSender emailSender,
            SpringTemplateEngine templateEngine) {
        return new MailSender(logoResource, cssResource, emailSender, templateEngine);
    }

    // ── Telegram (conditional) ────────────────────────────────────────────

    @Bean
    @ConditionalOnProperty(name = "service.telegram.enabled", havingValue = "true")
    public TelegramGateway telegramGateway(TelegramProperties properties,
                                           RestClient.Builder restClientBuilder) {
        return new TelegramGateway(properties, restClientBuilder);
    }

    @Bean
    @ConditionalOnProperty(name = "service.telegram.enabled", havingValue = "true")
    public TelegramNotificationService telegramNotificationService(TelegramGateway telegramGateway, TelegramProperties telegramProperties) {
        return new TelegramNotificationService(telegramGateway, telegramProperties);
    }

    // ── Email notification service ────────────────────────────────────────

    @Bean
    public EmailNotificationService emailNotificationService(MailSender mailSender) {
        return new EmailNotificationService(mailSender);
    }

    // ── Cross-module event handler ─────────────────────────────────────────

    @Bean
    public NotificationEventHandler notificationEventHandler(
            NotificationEventDispatcher notificationEventDispatcher) {
        return new NotificationEventHandler(notificationEventDispatcher);
    }

    @Bean
    public NotificationEventDispatcher notificationEventDispatcher(
            List<NotificationService> notificationServices,
            List<NotificationEventProcessor<?>> notificationEventProcessors) {
        return new NotificationEventDispatcher(notificationServices, notificationEventProcessors);
    }

    @Bean
    public UserRegisteredNotificationProcessor userRegisteredNotificationProcessor() {
        return new UserRegisteredNotificationProcessor();
    }

    @Bean
    public AccountActivationEmailRequestedNotificationProcessor accountActivationEmailRequestedNotificationProcessor() {
        return new AccountActivationEmailRequestedNotificationProcessor();
    }

    @Bean
    public AccountActivatedNotificationProcessor accountActivatedNotificationProcessor() {
        return new AccountActivatedNotificationProcessor();
    }

    @Bean
    public EmailCodeRequestedNotificationProcessor emailCodeRequestedNotificationProcessor() {
        return new EmailCodeRequestedNotificationProcessor();
    }

    @Bean
    public JourneyAnniversaryNotificationProcessor journeyAnniversaryNotificationProcessor(
            ApplicationProperties applicationProperties) {
        return new JourneyAnniversaryNotificationProcessor(applicationProperties);
    }
}
