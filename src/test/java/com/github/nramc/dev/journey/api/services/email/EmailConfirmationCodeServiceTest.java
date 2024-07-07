package com.github.nramc.dev.journey.api.services.email;

import com.github.nramc.dev.journey.api.models.core.ConfirmationCodeType;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeEntity;
import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeRepository;
import com.github.nramc.dev.journey.api.services.MailService;
import com.github.nramc.dev.journey.api.web.exceptions.TechnicalException;
import com.github.nramc.dev.journey.api.web.resources.rest.users.security.email.UserSecurityEmailAddressAttributeService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationUseCase.VERIFY_EMAIL_ADDRESS;
import static com.github.nramc.dev.journey.api.services.email.EmailConfirmationCodeService.CODE_LENGTH;
import static com.github.nramc.dev.journey.api.services.email.EmailConfirmationCodeService.EMAIL_CODE_TEMPLATE_HTML;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.AUTH_USER;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.EMAIL_ATTRIBUTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
            .receiver(EMAIL_ATTRIBUTE.value())
            .useCase(VERIFY_EMAIL_ADDRESS)
            .createdAt(LocalDateTime.now())
            .build();
    @Mock
    private MailService mailService;
    @Mock
    private ConfirmationCodeRepository codeRepository;
    @Mock
    private UserSecurityEmailAddressAttributeService emailAddressAttributeService;
    private EmailConfirmationCodeService emailConfirmationCodeService;


    @BeforeEach
    void setup() {
        emailConfirmationCodeService = new EmailConfirmationCodeService(mailService, codeRepository,
                new EmailCodeValidator(codeRepository, emailAddressAttributeService), emailAddressAttributeService);
    }

    @Test
    void send_whenDataValid_shouldSendEmailCodeSuccessfully() throws MessagingException {
        doNothing().when(mailService).sendEmailUsingTemplate(anyString(), anyString(), anyString(), any());
        when(emailAddressAttributeService.provideEmailAttributeIfExists(any(AuthUser.class)))
                .thenReturn(Optional.of(EMAIL_ATTRIBUTE));

        assertDoesNotThrow(() -> emailConfirmationCodeService.send(AUTH_USER, VERIFY_EMAIL_ADDRESS));

        verify(mailService).sendEmailUsingTemplate(
                eq(EMAIL_CODE_TEMPLATE_HTML),
                eq(EMAIL_ATTRIBUTE.value()),
                eq("Journey: Email Verification Request"),
                assertArg(params -> {
                    assertEquals(AUTH_USER.getName(), params.get("name"));
                    assertTrue(params.containsKey("ottPin"));
                })
        );
        verify(codeRepository).save(assertArg(entity -> {
            assertEquals(AUTH_USER.getUsername(), entity.getUsername());
            assertEquals(EMAIL_ATTRIBUTE.value(), entity.getReceiver());
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
        when(emailAddressAttributeService.provideEmailAttributeIfExists(any(AuthUser.class)))
                .thenReturn(Optional.of(EMAIL_ATTRIBUTE));
        assertThatExceptionOfType(TechnicalException.class).isThrownBy(() -> emailConfirmationCodeService.send(AUTH_USER, VERIFY_EMAIL_ADDRESS));
        verifyNoInteractions(codeRepository);
    }

    @RepeatedTest(10)
    void generateEmailCode() {
        String code = emailConfirmationCodeService.generateEmailCode().code();
        assertThat(code).asString()
                .isNotBlank()
                .containsOnlyDigits()
                .doesNotContainAnyWhitespaces()
                .hasSize(CODE_LENGTH);
    }

    @Test
    void verify_whenEmailCodeValid_shouldReturnSuccessAndInvalidAllExistingCodes() {
        when(codeRepository.findByUsernameAndCode(anyString(), anyString())).thenReturn(VALID_CODE_ENTITY);
        when(emailAddressAttributeService.provideEmailAttributeIfExists(any(AuthUser.class)))
                .thenReturn(Optional.of(EMAIL_ATTRIBUTE));
        boolean valid = emailConfirmationCodeService.verify(VALID_CODE, AUTH_USER, VERIFY_EMAIL_ADDRESS);
        assertThat(valid).isTrue();
        verify(codeRepository).findByUsernameAndCode(AUTH_USER.getUsername(), VALID_CODE.code());
        verify(codeRepository).deleteAll(any());
        verify(emailAddressAttributeService).setVerifiedStatus(eq(true), any(AuthUser.class));
    }

    @Test
    void verify_whenEmailCodeNotValid_shouldReturnError() {
        when(codeRepository.findByUsernameAndCode(anyString(), anyString())).thenReturn(VALID_CODE_ENTITY.toBuilder().isActive(false).build());
        boolean valid = emailConfirmationCodeService.verify(VALID_CODE, AUTH_USER, VERIFY_EMAIL_ADDRESS);
        assertThat(valid).isFalse();
        verify(codeRepository).findByUsernameAndCode(AUTH_USER.getUsername(), VALID_CODE.code());
        verify(codeRepository, never()).deleteAll(any());
        verify(emailAddressAttributeService, never()).setVerifiedStatus(anyBoolean(), any());
    }

}
