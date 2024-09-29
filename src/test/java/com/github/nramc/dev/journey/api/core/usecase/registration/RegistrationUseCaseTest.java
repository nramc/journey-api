package com.github.nramc.dev.journey.api.core.usecase.registration;

import com.github.nramc.dev.journey.api.config.TestConfig;
import com.github.nramc.dev.journey.api.config.security.Role;
import com.github.nramc.dev.journey.api.core.model.AppUser;
import com.github.nramc.dev.journey.api.core.usecase.notification.EmailNotificationUseCase;
import com.github.nramc.dev.journey.api.web.exceptions.BusinessException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static com.github.nramc.dev.journey.api.config.TestConfig.AUTHENTICATED_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class, ValidationAutoConfiguration.class, BCryptPasswordEncoder.class})
class RegistrationUseCaseTest {
    private static final AppUser ONBOARDING_USER = AppUser.builder()
            .name("Chalese Bitner")
            .username("juniper_eliasxcsx@cultural.ycw")
            .password("3GBWiBosweeZX7VSdnKgIBSp!")
            .roles(Set.of(Role.AUTHENTICATED_USER))
            .build();
    @Autowired
    private UserDetailsManager userDetailsManager;
    @Autowired
    private Validator validator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AccountActivationUseCase accountActivationUseCase;
    @MockBean
    private EmailNotificationUseCase emailNotificationUseCase;

    private RegistrationUseCase registrationUseCase;

    @BeforeEach
    void setUp() {
        registrationUseCase = new RegistrationUseCase(userDetailsManager, passwordEncoder, validator, accountActivationUseCase, emailNotificationUseCase);
    }

    @Test
    void register_whenRegistrationDataValid_thenShouldRegisterUser() {
        AppUser registeredUser = registrationUseCase.register(ONBOARDING_USER);
        assertThat(registeredUser).isNotNull()
                .satisfies(user -> assertThat(user.name()).isEqualTo(ONBOARDING_USER.name()))
                .satisfies(user -> assertThat(user.username()).isEqualTo(ONBOARDING_USER.username()))
                .satisfies(user -> assertThat(user.password()).isNotBlank())
                .satisfies(user -> assertThat(user.roles()).isEqualTo(Set.of(Role.AUTHENTICATED_USER)))
                .satisfies(user -> assertThat(user.enabled()).isFalse())
                .satisfies(user -> assertThat(user.mfaEnabled()).isFalse())
                .satisfies(user -> assertThat(user.createdDate()).isCloseTo(LocalDateTime.now(), within(10, ChronoUnit.MINUTES)));
        verify(emailNotificationUseCase).notifyAdmin("New User signup - juniper_eliasxcsx@cultural.ycw");
    }

    @Test
    void register_whenRegistrationDetailsNotValid_thenShouldThrowException() {
        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() ->
                registrationUseCase.register(ONBOARDING_USER.toBuilder().username("invalid-user-name").build()));
    }

    @Test
    void register_whenUserAlreadyExists_thenShouldThrowException() {
        assertThatExceptionOfType(BusinessException.class).isThrownBy(() ->
                registrationUseCase.register(ONBOARDING_USER.toBuilder().username(AUTHENTICATED_USER.getUsername()).build()));
    }

}
