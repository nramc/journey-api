package com.github.nramc.dev.journey.api.services.email;

import com.github.nramc.dev.journey.api.models.core.ConfirmationCodeType;
import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeEntity;
import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeRepository;
import com.github.nramc.dev.journey.api.services.MailService;
import com.github.nramc.dev.journey.api.web.exceptions.TechnicalException;
import jakarta.mail.MessagingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationUseCase.VERIFY_EMAIL_ADDRESS;
import static com.github.nramc.dev.journey.api.services.email.EmailConfirmationCodeService.CODE_LENGTH;
import static com.github.nramc.dev.journey.api.services.email.EmailConfirmationCodeService.EMAIL_CODE_TEMPLATE_HTML;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.AUTH_USER;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailConfirmationCodeServiceTest {
    private static final EmailCode VALID_CODE = EmailCode.valueOf(123456);
    private static final ConfirmationCodeEntity VALID_CODE_ENTITY = ConfirmationCodeEntity.builder()
            .id("ecc76991-0137-4152-b3b2-efce70a37ed0")
            .isActive(true)
            .username(AUTH_USER.getUsername())
            .type(ConfirmationCodeType.EMAIL_CODE)
            .code(VALID_CODE.code())
            .receiver(AUTH_USER.getEmailAddress())
            .useCase(VERIFY_EMAIL_ADDRESS)
            .createdAt(LocalDateTime.now())
            .build();
    @Mock
    private MailService mailService;
    @Mock
    private ConfirmationCodeRepository codeRepository;
    private EmailConfirmationCodeService emailConfirmationCodeService;


    @BeforeEach
    void setup() {
        emailConfirmationCodeService = new EmailConfirmationCodeService(mailService, codeRepository, new EmailCodeValidator(codeRepository));
    }

    @Test
    void send_whenDataValid_shouldSendEmailCodeSuccessfully() throws MessagingException {
        doNothing().when(mailService).sendEmailUsingTemplate(anyString(), anyString(), anyString(), any());

        assertDoesNotThrow(() -> emailConfirmationCodeService.send(AUTH_USER, VERIFY_EMAIL_ADDRESS));

        verify(mailService).sendEmailUsingTemplate(
                eq(EMAIL_CODE_TEMPLATE_HTML),
                eq(AUTH_USER.getEmailAddress()),
                eq("Journey: Email Verification Request"),
                assertArg(params -> {
                    assertEquals(AUTH_USER.getName(), params.get("name"));
                    assertTrue(params.containsKey("ottPin"));
                })
        );
        verify(codeRepository).save(assertArg(entity -> {
            assertEquals(AUTH_USER.getUsername(), entity.getUsername());
            assertEquals(AUTH_USER.getEmailAddress(), entity.getReceiver());
            assertEquals(VERIFY_EMAIL_ADDRESS, entity.getUseCase());
            assertEquals(ConfirmationCodeType.EMAIL_CODE, entity.getType());
            assertNotNull(entity.getId());
            assertNotNull(entity.getCode());
            assertNotNull(entity.getCreatedAt());
        }));
    }

    @Test
    void send_whenSendingEmailCodeFailed_shouldThrowError() throws MessagingException {
        doThrow(new RuntimeException("mocked")).when(mailService).sendEmailUsingTemplate(anyString(), anyString(), anyString(), any());
        assertThrows(TechnicalException.class, () -> emailConfirmationCodeService.send(AUTH_USER, VERIFY_EMAIL_ADDRESS));
        verifyNoInteractions(codeRepository);
    }

    @RepeatedTest(10)
    void generateEmailCode() {
        String code = emailConfirmationCodeService.generateEmailCode().code();
        Assertions.assertThat(code).asString()
                .isNotBlank()
                .containsOnlyDigits()
                .doesNotContainAnyWhitespaces()
                .hasSize(CODE_LENGTH);
    }

    @Test
    void verify_whenEmailCodeValid_shouldReturnSuccessAndInvalidAllExistingCodes() {
        when(codeRepository.findByUsernameAndCode(anyString(), anyString())).thenReturn(VALID_CODE_ENTITY);
        boolean valid = emailConfirmationCodeService.verify(VALID_CODE, AUTH_USER, VERIFY_EMAIL_ADDRESS);
        assertTrue(valid);
        verify(codeRepository).findByUsernameAndCode(AUTH_USER.getUsername(), VALID_CODE.code());
        verify(codeRepository).deleteAll(any());
    }

    @Test
    void verify_whenEmailCodeNotValid_shouldReturnError() {
        when(codeRepository.findByUsernameAndCode(anyString(), anyString())).thenReturn(VALID_CODE_ENTITY.toBuilder().isActive(false).build());
        boolean valid = emailConfirmationCodeService.verify(VALID_CODE, AUTH_USER, VERIFY_EMAIL_ADDRESS);
        assertFalse(valid);
        verify(codeRepository).findByUsernameAndCode(AUTH_USER.getUsername(), VALID_CODE.code());
        verify(codeRepository, never()).deleteAll(any());
    }

}
