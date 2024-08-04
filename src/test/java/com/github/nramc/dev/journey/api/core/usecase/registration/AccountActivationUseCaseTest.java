package com.github.nramc.dev.journey.api.core.usecase.registration;

import com.github.nramc.dev.journey.api.config.ApplicationProperties;
import com.github.nramc.dev.journey.api.config.security.Role;
import com.github.nramc.dev.journey.api.core.model.AppUser;
import com.github.nramc.dev.journey.api.core.model.EmailToken;
import com.github.nramc.dev.journey.api.core.services.EmailTokenService;
import com.github.nramc.dev.journey.api.gateway.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountActivationUseCaseTest {
    private static final AppUser ONBOARDING_USER = AppUser.builder()
            .username("ikaika_kinsleyh6he@harold.bpk")
            .name("Tacia Bos")
            .enabled(false)
            .roles(Set.of(Role.AUTHENTICATED_USER))
            .build();
    private final static EmailToken EMAIL_TOKEN = EmailToken.valueOf("2fbbd48c-c16a-4638-9bce-988502cc6f11");
    @Mock
    private ApplicationProperties applicationProperties;
    @Mock
    private EmailTokenService emailTokenService;
    @Mock
    private MailService mailService;
    @InjectMocks
    private AccountActivationUseCase accountActivationUseCase;

    @Test
    void sendActivationEmail() {
        when(applicationProperties.uiAppUrl()).thenReturn("https://nramc.github.io/journeys");
        when(emailTokenService.generateEmailToken(ONBOARDING_USER)).thenReturn(EMAIL_TOKEN);

        accountActivationUseCase.sendActivationEmail(ONBOARDING_USER);
        verify(mailService).sendSimpleEmail(anyString(), anyString(), anyString());
    }

}