package com.github.nramc.dev.journey.api.core.usecase.registration;

import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.exceptions.BusinessException;
import com.github.nramc.dev.journey.api.core.usecase.notification.EmailNotificationUseCase;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.time.LocalDateTime;
import java.util.Set;

import static com.github.nramc.dev.journey.api.repository.user.AppUserConvertor.toEntity;

@Slf4j
@RequiredArgsConstructor
public class RegistrationUseCase {
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final AccountActivationUseCase accountActivationUseCase;
    private final EmailNotificationUseCase emailNotificationUseCase;

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

        AuthUser userEntity = toEntity(onboardingUser);
        userDetailsManager.createUser(userEntity);
        accountActivationUseCase.sendActivationEmail(onboardingUser);
        emailNotificationUseCase.notifyAdmin("New User signup - " + onboardingUser.username());

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
