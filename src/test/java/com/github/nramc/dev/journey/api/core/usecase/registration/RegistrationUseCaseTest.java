package com.github.nramc.dev.journey.api.core.usecase.registration;

import com.github.nramc.dev.journey.api.config.TestConfig;
import com.github.nramc.dev.journey.api.config.security.Role;
import com.github.nramc.dev.journey.api.core.model.AppUser;
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

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class, ValidationAutoConfiguration.class, BCryptPasswordEncoder.class})
class RegistrationUseCaseTest {
    private static final AppUser ONBOARDING_USER = AppUser.builder()
            .name("Chalese Bitner")
            .username("juniper_eliasxcsx@cultural.ycw")
            .password("3GBWiBosweeZX7VSdnKgIBSp!")
            .build();
    private static final AppUser NEW_USER = AppUser.builder()
            .name("Denika Sublett")
            .username("latavia_hellmandczl@tribute.lgh")
            .password("ElLpNbOJ25M71SZGyaXsz5H!")
            .roles(Set.of(Role.AUTHENTICATED_USER, Role.MAINTAINER))
            .build();
    @Autowired
    private UserDetailsManager userDetailsManager;
    @Autowired
    private Validator validator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AccountActivationUseCase accountActivationUseCase;

    private RegistrationUseCase registrationUseCase;

    @BeforeEach
    void setUp() {
        registrationUseCase = new RegistrationUseCase(userDetailsManager, passwordEncoder, validator, accountActivationUseCase);
    }

    @Test
    void register_whenRegistrationDataValid_thenShouldRegisterUser() {
        AppUser registeredUser = registrationUseCase.register(ONBOARDING_USER);
        assertThat(registeredUser).isNotNull()
                .satisfies(user -> assertThat(user.name()).isEqualTo(ONBOARDING_USER.name()))
                .satisfies(user -> assertThat(user.username()).isEqualTo(ONBOARDING_USER.username()))
                .satisfies(user -> assertThat(user.password()).isNullOrEmpty())
                .satisfies(user -> assertThat(user.roles()).isEqualTo(Set.of(Role.AUTHENTICATED_USER)))
                .satisfies(user -> assertThat(user.enabled()).isFalse())
                .satisfies(user -> assertThat(user.mfaEnabled()).isFalse())
                .satisfies(user -> assertThat(user.createdDate()).isCloseTo(LocalDateTime.now(), within(10, ChronoUnit.MINUTES)));
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

    @Test
    void create_whenRegistrationDataValid_thenShouldRegisterUser() {
        AppUser createdUser = registrationUseCase.create(NEW_USER);
        assertThat(createdUser).isNotNull()
                .satisfies(user -> assertThat(user.name()).isEqualTo(NEW_USER.name()))
                .satisfies(user -> assertThat(user.username()).isEqualTo(NEW_USER.username()))
                .satisfies(user -> assertThat(user.password()).isNullOrEmpty())
                .satisfies(user -> assertThat(user.roles()).isEqualTo(NEW_USER.roles()))
                .satisfies(user -> assertThat(user.enabled()).isTrue())
                .satisfies(user -> assertThat(user.mfaEnabled()).isFalse())
                .satisfies(user -> assertThat(user.createdDate()).isCloseTo(LocalDateTime.now(), within(10, ChronoUnit.MINUTES)));
    }

    @Test
    void create_whenRegistrationDetailsNotValid_thenShouldThrowException() {
        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() ->
                registrationUseCase.create(NEW_USER.toBuilder().username("invalid-user-name").build()));
    }

    @Test
    void create_whenUserAlreadyExists_thenShouldThrowException() {
        assertThatExceptionOfType(BusinessException.class).isThrownBy(() ->
                registrationUseCase.create(NEW_USER.toBuilder().username(AUTHENTICATED_USER.getUsername()).build()));
    }

}