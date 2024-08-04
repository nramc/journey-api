package com.github.nramc.dev.journey.api.core.usecase.registration;

import com.github.nramc.dev.journey.api.config.security.Role;
import com.github.nramc.dev.journey.api.core.model.AppUser;
import com.github.nramc.dev.journey.api.gateway.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class AccountActivationUseCaseTest {
    private static final AppUser ONBOARDING_USER = AppUser.builder()
            .username("ikaika_kinsleyh6he@harold.bpk")
            .name("Tacia Bos")
            .enabled(false)
            .roles(Set.of(Role.AUTHENTICATED_USER))
            .build();
    @Mock
    private MailService mailService;
    @InjectMocks
    private AccountActivationUseCase accountActivationUseCase;

    @Test
    void sendActivationEmail() {
        accountActivationUseCase.sendActivationEmail(ONBOARDING_USER);
        Mockito.verify(mailService).sendSimpleEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

}