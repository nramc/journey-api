package com.github.nramc.dev.journey.api.account.usecase;

import com.github.nramc.dev.journey.api.account.UserRegisteredEvent;
import com.github.nramc.dev.journey.api.account.repository.AppUserConvertor;
import com.github.nramc.dev.journey.api.account.repository.AuthUser;
import com.github.nramc.dev.journey.api.shared.domain.AppUser;
import com.github.nramc.dev.journey.api.shared.exceptions.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class RegistrationUseCase {

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final AccountActivationUseCase accountActivationUseCase;
    private final ApplicationEventPublisher applicationEvents;

    public AppUser register(AppUser user) {
        validate(user);

        AppUser onboardingUser = user.toBuilder()
                .username(user.username().toLowerCase())
                .password(passwordEncoder.encode(user.password()))
                .roles(user.roles())
                .enabled(false)
                .createdDate(LocalDateTime.now())
                .passwordChangedAt(LocalDateTime.now())
                .build();

        AuthUser userEntity = AppUserConvertor.toEntity(onboardingUser);
        userDetailsManager.createUser(userEntity);
        accountActivationUseCase.sendActivationEmail(onboardingUser);
        applicationEvents.publishEvent(new UserRegisteredEvent(onboardingUser.username()));

        return onboardingUser;
    }

    private void validate(AppUser user) {
        Set<ConstraintViolation<AppUser>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        if (userDetailsManager.userExists(user.username())) {
            throw new BusinessException("Username already exists", "user.exists");
        }
    }
}
