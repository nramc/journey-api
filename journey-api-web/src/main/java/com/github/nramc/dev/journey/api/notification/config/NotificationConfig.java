package com.github.nramc.dev.journey.api.notification.config;

import com.github.nramc.dev.journey.api.notification.NotificationEventHandler;
import com.github.nramc.dev.journey.api.notification.NotificationService;
import com.github.nramc.dev.journey.api.notification.email.EmailNotificationService;
import com.github.nramc.dev.journey.api.notification.gateway.telegram.TelegramGateway;
import com.github.nramc.dev.journey.api.notification.gateway.telegram.TelegramProperties;
import com.github.nramc.dev.journey.api.notification.mail.MailService;
import com.github.nramc.dev.journey.api.notification.telegram.TelegramNotificationService;
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
@EnableConfigurationProperties(TelegramProperties.class)
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
    public MailService mailService(
            @Value("classpath:/assets/logo.png") Resource logoResource,
            @Value("classpath:/mail-templates/layout/style.css") Resource cssResource,
            JavaMailSender emailSender,
            SpringTemplateEngine templateEngine) {
        return new MailService(logoResource, cssResource, emailSender, templateEngine);
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
    public TelegramNotificationService telegramNotificationService(TelegramGateway telegramGateway) {
        return new TelegramNotificationService(telegramGateway);
    }

    // ── Email notification service ────────────────────────────────────────

    @Bean
    public EmailNotificationService emailNotificationService(MailService mailService) {
        return new EmailNotificationService(mailService);
    }

    // ── Cross-module event handler ─────────────────────────────────────────

    @Bean
    public NotificationEventHandler notificationEventHandler(List<NotificationService> notificationServices) {
        return new NotificationEventHandler(notificationServices);
    }
}
