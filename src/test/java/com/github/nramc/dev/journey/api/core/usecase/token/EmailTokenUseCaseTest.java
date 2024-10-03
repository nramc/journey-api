package com.github.nramc.dev.journey.api.core.usecase.token;

import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.domain.EmailToken;
import com.github.nramc.dev.journey.api.core.usecase.code.EmailTokenUseCase;
import com.github.nramc.dev.journey.api.repository.user.ConfirmationCodeEntity;
import com.github.nramc.dev.journey.api.repository.user.ConfirmationCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.github.nramc.dev.journey.api.core.domain.user.ConfirmationCodeType.EMAIL_CODE;
import static com.github.nramc.dev.journey.api.core.domain.user.ConfirmationCodeType.EMAIL_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailTokenUseCaseTest {
    private static final EmailToken VALID_EMAIL_TOKEN = EmailToken.valueOf("7c26f3d9-6b22-4a58-ac2b-77638082e46c");
    private static final AppUser APP_USER = AppUser.builder()
            .name("Jaquelin Geier")
            .username("doran_teelobo@turned.hr")
            .build();
    private static final ConfirmationCodeEntity CONFIRMATION_CODE = ConfirmationCodeEntity.builder()
            .id("1580800893")
            .type(EMAIL_TOKEN)
            .code(VALID_EMAIL_TOKEN.token())
            .isActive(true)
            .build();
    @Mock
    private ConfirmationCodeRepository codeRepository;
    @InjectMocks
    private EmailTokenUseCase emailTokenUseCase;

    @Test
    void generateEmailToken_shouldGenerateEmailToken_andPersisted() {
        EmailToken emailToken = emailTokenUseCase.generateEmailToken(APP_USER);
        verify(codeRepository).save(assertArg(confirmationCodeEntity -> assertThat(confirmationCodeEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getCode()).isEqualTo(emailToken.token()))
                .satisfies(entity -> assertThat(entity.getUsername()).isEqualTo(APP_USER.username()))
                .satisfies(entity -> assertThat(entity.getType()).isEqualTo(EMAIL_TOKEN))
                .satisfies(entity -> assertThat(entity.isActive()).isTrue())
        ));
    }

    @Test
    void verifyEmailToken_whenTokenExistsAndValid_shouldReturnTrue() {
        when(codeRepository.findAllByUsername(APP_USER.username())).thenReturn(
                List.of(CONFIRMATION_CODE)
        );
        assertThat(emailTokenUseCase.verifyEmailToken(VALID_EMAIL_TOKEN, APP_USER)).isTrue();
    }

    @Test
    void verifyEmailToken_whenTokenNotExists_shouldReturnFalse() {
        when(codeRepository.findAllByUsername(APP_USER.username())).thenReturn(
                List.of()
        );
        assertThat(emailTokenUseCase.verifyEmailToken(VALID_EMAIL_TOKEN, APP_USER)).isFalse();
    }

    @Test
    void verifyEmailToken_whenTokenExistsButValid_shouldReturnFalse() {
        when(codeRepository.findAllByUsername(APP_USER.username())).thenReturn(
                List.of(CONFIRMATION_CODE.toBuilder().isActive(false).build())
        );
        assertThat(emailTokenUseCase.verifyEmailToken(VALID_EMAIL_TOKEN, APP_USER)).isFalse();
    }

    @Test
    void verifyEmailToken_whenTokenExistsButTypeNotMatched_shouldReturnFalse() {
        when(codeRepository.findAllByUsername(APP_USER.username())).thenReturn(
                List.of(CONFIRMATION_CODE.toBuilder().type(EMAIL_CODE).build())
        );
        assertThat(emailTokenUseCase.verifyEmailToken(VALID_EMAIL_TOKEN, APP_USER)).isFalse();
    }

    @Test
    void verifyEmailToken_whenTokenExistsButValueNotMatched_shouldReturnFalse() {
        when(codeRepository.findAllByUsername(APP_USER.username())).thenReturn(
                List.of(CONFIRMATION_CODE.toBuilder().code("078275ea-e9ff-4043-9275-50a84802f205").build())
        );
        assertThat(emailTokenUseCase.verifyEmailToken(VALID_EMAIL_TOKEN, APP_USER)).isFalse();
    }

}
