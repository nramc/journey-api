package com.github.nramc.dev.journey.api.account.codes.emailcode;

import com.github.nramc.dev.journey.api.account.EmailCodeRequestedEvent;
import com.github.nramc.dev.journey.api.account.codes.EmailCode;
import com.github.nramc.dev.journey.api.account.repository.code.ConfirmationCodeEntity;
import com.github.nramc.dev.journey.api.account.repository.code.ConfirmationCodeRepository;
import com.github.nramc.dev.journey.api.shared.domain.user.ConfirmationCodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.nramc.dev.journey.api.account.codes.emailcode.EmailCodeUseCase.CODE_LENGTH;
import static com.github.nramc.dev.journey.api.account.web.users.UsersData.AUTHENTICATED_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailCodeUseCaseTest {
    private static final EmailCode VALID_CODE = EmailCode.valueOf(123456);
    private static final ConfirmationCodeEntity VALID_CODE_ENTITY = ConfirmationCodeEntity.builder()
            .id("ecc76991-0137-4152-b3b2-efce70a37ed0")
            .isActive(true)
            .username(AUTHENTICATED_USER.getUsername())
            .type(ConfirmationCodeType.EMAIL_CODE)
            .code(VALID_CODE.code())
            .createdAt(LocalDateTime.now())
            .build();
    @Mock
    private ConfirmationCodeRepository codeRepository;
    @Mock
    private ApplicationEventPublisher applicationEvents;
    private EmailCodeUseCase emailCodeUseCase;


    @BeforeEach
    void setup() {
        emailCodeUseCase = new EmailCodeUseCase(codeRepository, new EmailCodeValidator(codeRepository), applicationEvents);
    }

    @Test
    void send_whenDataValid_shouldSendEmailCodeEventSuccessfully() {

        assertThatCode(() -> emailCodeUseCase.send(AUTHENTICATED_USER)).doesNotThrowAnyException();

        verify(applicationEvents).publishEvent(assertArg((Object event) -> {
            assertThat(event).isInstanceOf(EmailCodeRequestedEvent.class);
            EmailCodeRequestedEvent emailEvent = (EmailCodeRequestedEvent) event;
            assertThat(emailEvent.username()).isEqualTo(AUTHENTICATED_USER.getUsername());
            assertThat(emailEvent.metadata()).containsEntry("name", AUTHENTICATED_USER.getName())
                    .containsKey("ottPin");
        }));
        verify(codeRepository).save(assertArg(entity -> {
            assertThat(entity.getUsername()).isEqualTo(AUTHENTICATED_USER.getUsername());
            assertThat(entity.getType()).isEqualTo(ConfirmationCodeType.EMAIL_CODE);
            assertThat(entity.getId()).isNotNull();
            assertThat(entity.getCode()).isNotNull();
            assertThat(entity.getCreatedAt()).isNotNull();
        }));
    }

    @RepeatedTest(10)
    void generateEmailCode() {
        String code = emailCodeUseCase.generateEmailCode().code();
        assertThat(code).asString()
                .isNotBlank()
                .containsOnlyDigits()
                .doesNotContainAnyWhitespaces()
                .hasSize(CODE_LENGTH);
    }

    @Test
    void verify_whenEmailCodeValid_shouldReturnSuccessAndInvalidAllExistingCodes() {
        when(codeRepository.findAllByUsername(anyString())).thenReturn(List.of(VALID_CODE_ENTITY));

        boolean valid = emailCodeUseCase.verify(VALID_CODE, AUTHENTICATED_USER);

        assertThat(valid).isTrue();
        verify(codeRepository, atLeastOnce()).findAllByUsername(AUTHENTICATED_USER.getUsername());
        verify(codeRepository).deleteAll(any());
    }

    @Test
    void verify_whenEmailCodeNotValid_shouldReturnError() {
        when(codeRepository.findAllByUsername(anyString())).thenReturn(List.of(VALID_CODE_ENTITY.toBuilder().isActive(false).build()));
        boolean valid = emailCodeUseCase.verify(VALID_CODE, AUTHENTICATED_USER);
        assertThat(valid).isFalse();
        verify(codeRepository).findAllByUsername(AUTHENTICATED_USER.getUsername());
        verify(codeRepository, never()).deleteAll(any());
    }

}
