package com.github.nramc.dev.journey.api.account.usecase;

import com.github.nramc.dev.journey.api.infrastructure.security.InMemoryUserDetailsConfig;
import com.github.nramc.dev.journey.api.shared.domain.AppUser;
import com.github.nramc.dev.journey.api.shared.domain.user.security.Role;
import com.github.nramc.dev.journey.api.shared.exceptions.BusinessException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static com.github.nramc.dev.journey.api.account.web.users.UsersData.AUTHENTICATED_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {InMemoryUserDetailsConfig.class, ValidationAutoConfiguration.class, BCryptPasswordEncoder.class})
class RegistrationUseCaseTest {
    private static final AppUser ONBOARDING_USER = AppUser.builder()
            .name("Chalese Bitner")
            .username("juniper_eliasxcsx@cultural.ycw")
            .password("3GBWiBosweeZX7VSdnKgIBSp!")
            .roles(Set.of(Role.AUTHENTICATED_USER))
            .build();
    @Autowired
    UserDetailsManager userDetailsManager;
    @Autowired
    Validator validator;
    @Autowired
    PasswordEncoder passwordEncoder;
    @MockitoBean
    AccountActivationUseCase accountActivationUseCase;
    @MockitoBean
    ApplicationEventPublisher applicationEvents;

    private RegistrationUseCase registrationUseCase;

    @BeforeEach
    void setUp() {
        registrationUseCase = new RegistrationUseCase(userDetailsManager, passwordEncoder, validator, accountActivationUseCase, applicationEvents);
    }

    @Test
    void register_whenRegistrationDataValid_thenShouldRegisterUser() {
        AppUser registeredUser = registrationUseCase.register(ONBOARDING_USER);
        assertThat(registeredUser).isNotNull()
                .satisfies(user -> assertThat(user.name()).isEqualTo(ONBOARDING_USER.name()))
                .satisfies(user -> assertThat(user.username()).isEqualTo(ONBOARDING_USER.username()))
                .satisfies(user -> assertThat(user.password()).isNotBlank())
                .satisfies(user -> assertThat(user.roles()).hasSameElementsAs(Set.of(Role.AUTHENTICATED_USER)))
                .satisfies(user -> assertThat(user.enabled()).isFalse())
                .satisfies(user -> assertThat(user.mfaEnabled()).isFalse())
                .satisfies(user -> assertThat(user.createdDate()).isCloseTo(LocalDateTime.now(), within(10, ChronoUnit.MINUTES)));
        verify(applicationEvents).publishEvent(any(Object.class));
    }

    @Test
    void register_whenRegistrationDetailsNotValid_thenShouldThrowException() {
        var user = ONBOARDING_USER.toBuilder().username("invalid-user-name").build();
        assertThatThrownBy(() -> registrationUseCase.register(user)).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void register_whenUserAlreadyExists_thenShouldThrowException() {
        var user = ONBOARDING_USER.toBuilder().username(AUTHENTICATED_USER.getUsername()).build();
        assertThatThrownBy(() -> registrationUseCase.register(user)).isInstanceOf(BusinessException.class);
    }

}
