package com.github.nramc.dev.journey.api.core.services;

import com.github.nramc.dev.journey.api.core.model.AppUser;
import com.github.nramc.dev.journey.api.core.model.EmailToken;
import com.github.nramc.dev.journey.api.repository.security.ConfirmationCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.nramc.dev.journey.api.core.security.attributes.recovery.code.ConfirmationCodeType.EMAIL_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.assertArg;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailTokenServiceTest {
    private static final AppUser APP_USER = AppUser.builder()
            .name("Jaquelin Geier")
            .username("doran_teelobo@turned.hr")
            .build();
    @Mock
    private ConfirmationCodeRepository codeRepository;
    @InjectMocks
    private EmailTokenService emailTokenService;

    @Test
    void generateEmailToken_shouldGenerateEmailToken_andPersisted() {
        EmailToken emailToken = emailTokenService.generateEmailToken(APP_USER);
        verify(codeRepository).save(assertArg(confirmationCodeEntity -> assertThat(confirmationCodeEntity).isNotNull()
                .satisfies(entity -> assertThat(entity.getCode()).isEqualTo(emailToken.token()))
                .satisfies(entity -> assertThat(entity.getUsername()).isEqualTo(APP_USER.username()))
                .satisfies(entity -> assertThat(entity.getType()).isEqualTo(EMAIL_TOKEN))
                .satisfies(entity -> assertThat(entity.isActive()).isTrue())
        ));
    }

}