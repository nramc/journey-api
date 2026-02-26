package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.core.services.mail.MailService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
@Profile("!test")
public class MailConfig {

    @Bean("thymeleafEmailTemplateResolver")
    public ITemplateResolver thymeleafEmailTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("mail-templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine(
            @Qualifier("thymeleafEmailTemplateResolver") ITemplateResolver thymeleafEmailTemplateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(thymeleafEmailTemplateResolver);

        return templateEngine;
    }

    @Bean
    public MailService mailService(
            @Value("classpath:/assets/logo.png") Resource logoResource,
            @Value("classpath:/mail-templates/layout/style.css") Resource cssResource,
            JavaMailSender emailSender,
            SpringTemplateEngine templateEngine
    ) {
        return new MailService(logoResource, cssResource, emailSender, templateEngine);
    }

}
