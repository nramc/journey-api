package com.github.nramc.dev.journey.api.gateway;

import com.github.nramc.dev.journey.api.config.MailConfig;
import com.github.nramc.dev.journey.api.core.services.mail.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.nramc.dev.journey.api.core.usecase.codes.emailcode.EmailCodeUseCase.EMAIL_CODE_TEMPLATE_HTML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.assertArg;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest(classes = {
        MailConfig.class,
        MailSenderAutoConfiguration.class
})
@ActiveProfiles({"dev"})
class MailServiceTest {
    @Container
    static GenericContainer<?> mailpitContainer = new GenericContainer<>("axllent/mailpit:latest")
            .withExposedPorts(1025, 8025)
            .waitingFor(Wait.forLogMessage(".*accessible via.*", 1));

    @DynamicPropertySource
    static void configureMail(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", mailpitContainer::getHost);
        registry.add("spring.mail.port", mailpitContainer::getFirstMappedPort);
        registry.add("mailpit.web.port", () -> mailpitContainer.getMappedPort(8025));
    }

    @Autowired
    MailService mailService;

    @MockitoSpyBean
    JavaMailSender emailSender;

    @Test
    void context() {
        assertThat(mailService).isNotNull();
    }

    @Test
    void sendSimpleEmail_shouldSendEmailWithExpectation() {
        mailService.sendSimpleEmail(List.of("example-email@example.com"), "Example Subject", "Example Body");
        verify(emailSender).send(assertArg((SimpleMailMessage mailMessage) ->
                assertThat(mailMessage).isNotNull()
                        .satisfies(mail -> assertThat(mail.getTo()).hasSize(1).containsExactly("example-email@example.com"))
                        .satisfies(mail -> assertThat(mail.getSubject()).isEqualTo("Example Subject"))
                        .satisfies(mail -> assertThat(mail.getText()).isEqualTo("Example Body"))
        ));
    }

    @Test
    void sendEmailUsingTemplate_whenTemplateEmailCode_shouldSendEmailWithExpectation() throws MessagingException {
        String toEmailAddress = "example-email@example.com";
        Map<String, Object> placeholders = new HashMap<>();
        String name = "John Doe";
        placeholders.put("name", name);
        String emailCode = "223344";
        placeholders.put("ottPin", emailCode);
        mailService.sendEmailUsingTemplate(EMAIL_CODE_TEMPLATE_HTML, toEmailAddress, "Example Subject", placeholders);


        verify(emailSender).send(assertArg((MimeMessage mailMessage) -> {
                    assertThat(mailMessage).isNotNull()
                            .satisfies(mail -> assertThat(mail.getAllRecipients()).hasSize(1).containsExactly(InternetAddress.parse(toEmailAddress)))
                            .satisfies(mail -> assertThat(mail.getSubject()).isEqualTo("Example Subject"))
                            .satisfies(mail -> assertThat(mail.getContent()).isInstanceOf(Multipart.class))
                            .satisfies(mail -> assertThat(mail.getContent()).isInstanceOf(MimeMultipart.class));

                    try (ByteArrayOutputStream aos = new ByteArrayOutputStream()) {
                        MimeMultipart mp = (MimeMultipart) mailMessage.getContent();
                        MimeBodyPart part = (MimeBodyPart) ((MimeMultipart) mp.getBodyPart(0).getContent()).getBodyPart(0);
                        part.writeTo(aos);
                        assertThat(aos.toString()).contains(name, emailCode);
                        assertThat(part.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
                    }
                }
        ));
    }


}
