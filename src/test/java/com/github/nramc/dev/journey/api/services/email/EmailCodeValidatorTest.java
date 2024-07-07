package com.github.nramc.dev.journey.api.services.email;

import com.github.nramc.dev.journey.api.models.core.ConfirmationCodeType;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeEntity;
import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.github.nramc.dev.journey.api.services.confirmationcode.ConfirmationUseCase.VERIFY_EMAIL_ADDRESS;
import static com.github.nramc.dev.journey.api.services.email.EmailCodeValidator.EMAIL_CODE_VALIDITY_MINUTES;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.AUTH_USER;
import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.EMAIL_ATTRIBUTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailCodeValidatorTest {
    private static final EmailCode VALID_CODE = EmailCode.valueOf(747469);
    private static final AuthUser VALID_USER = AUTH_USER.toBuilder().build();
    private static final ConfirmationCodeEntity VALID_CODE_ENTITY = ConfirmationCodeEntity.builder()
            .id("ecc76991-0137-4152-b3b2-efce70a37ed0")
            .isActive(true)
            .username(VALID_USER.getUsername())
            .type(ConfirmationCodeType.EMAIL_CODE)
            .code(VALID_CODE.code())
            .receiver(EMAIL_ATTRIBUTE.value())
            .useCase(VERIFY_EMAIL_ADDRESS)
            .createdAt(LocalDateTime.now())
            .build();
    @Mock
    private ConfirmationCodeRepository codeRepository;
    private EmailCodeValidator emailCodeValidator;

    @BeforeEach
    void setup() {
        emailCodeValidator = new EmailCodeValidator(codeRepository);
    }

    @Test
    void isValid_whenCodeValid_shouldReturnTrue() {
        when(codeRepository.findByUsernameAndCode(VALID_USER.getUsername(), VALID_CODE.code())).thenReturn(VALID_CODE_ENTITY);
        assertThat(emailCodeValidator.isValid(VALID_CODE, VALID_USER)).isTrue();
    }

    @Test
    void isValid_whenCodeNotExists_shouldReturnFalse() {
        assertThat(emailCodeValidator.isValid(EmailCode.valueOf(222222), VALID_USER)).isFalse();
    }

    @Test
    void isValid_whenCodeNotActive_shouldReturnFalse() {
        when(codeRepository.findByUsernameAndCode(VALID_USER.getUsername(), VALID_CODE.code()))
                .thenReturn(VALID_CODE_ENTITY.toBuilder().isActive(false).build());
        assertThat(emailCodeValidator.isValid(VALID_CODE, VALID_USER)).isFalse();
    }

    @Test
    void isValid_whenConfirmationTypeNotMatched_shouldReturnFalse() {
        when(codeRepository.findByUsernameAndCode(VALID_USER.getUsername(), VALID_CODE.code()))
                .thenReturn(VALID_CODE_ENTITY.toBuilder().type(ConfirmationCodeType.EMAIL_TOKEN).build());
        assertThat(emailCodeValidator.isValid(VALID_CODE, VALID_USER)).isFalse();
    }

    @Test
    void isValid_whenCodeExpired_shouldReturnFalse() {
        when(codeRepository.findByUsernameAndCode(VALID_USER.getUsername(), VALID_CODE.code()))
                .thenReturn(VALID_CODE_ENTITY.toBuilder().createdAt(LocalDateTime.now().minusMinutes(EMAIL_CODE_VALIDITY_MINUTES + 1)).build());
        assertThat(emailCodeValidator.isValid(VALID_CODE, VALID_USER)).isFalse();
    }

}