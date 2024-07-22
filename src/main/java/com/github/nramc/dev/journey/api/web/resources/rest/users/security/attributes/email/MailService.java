package com.github.nramc.dev.journey.api.web.resources.rest.users.security.attributes.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class MailService {
    private final Resource logoResource;
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
        log.info("Simple Email has been sent successfully");
    }

    public void sendEmailUsingTemplate(String template, String to, String subject, Map<String, Object> variables)
            throws MessagingException {
        Context context = new Context();
        context.setVariables(variables);

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
