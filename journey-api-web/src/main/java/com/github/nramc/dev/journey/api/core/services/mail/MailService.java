package com.github.nramc.dev.journey.api.core.services.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
public class MailService {
    private final Resource logoResource;
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    private final String emailStyles;

    public MailService(Resource logoResource, Resource cssResource, JavaMailSender emailSender, SpringTemplateEngine templateEngine) {
        this.logoResource = logoResource;
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        try {
            this.emailStyles = cssResource != null ? cssResource.getContentAsString(StandardCharsets.UTF_8) : "";
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load email stylesheet", e);
        }
    }

    public void sendSimpleEmail(List<String> toAddresses, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toAddresses.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
        log.info("Simple Email has been sent successfully");
    }

    public void sendEmailUsingTemplate(String template, String to, String subject, Map<String, Object> placeholders)
            throws MessagingException {
        Context context = new Context();
        context.setVariables(placeholders);
        context.setVariable("styles", emailStyles);
        context.setVariable("copyrightYear", LocalDateTime.now().getYear());

        String htmlBody = templateEngine.process(template, context);

        sendHtmlEmail(to, subject, htmlBody);
        log.info("Email using template[{}] has been sent successfully", template);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.addInline("logo.png", logoResource);
        emailSender.send(message);
    }
}
