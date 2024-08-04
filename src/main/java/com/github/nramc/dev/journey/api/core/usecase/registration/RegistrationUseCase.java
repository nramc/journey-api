package com.github.nramc.dev.journey.api.core.usecase.registration;

import com.github.nramc.dev.journey.api.config.security.Role;
import com.github.nramc.dev.journey.api.core.converters.AppUserConvertor;
import com.github.nramc.dev.journey.api.core.model.AppUser;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.web.exceptions.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.time.LocalDateTime;
import java.util.Set;

import static com.github.nramc.dev.journey.api.core.converters.AppUserConvertor.toEntity;

@Slf4j
@RequiredArgsConstructor
public class RegistrationUseCase {
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    public AppUser register(AppUser user) {
        validate(user);

        AppUser onboardingUser = setDefaultValuesForRegistration(user);
        AuthUser userEntity = toEntity(onboardingUser);
        userDetailsManager.createUser(userEntity);
        AuthUser registeredUserEntity = (AuthUser) userDetailsManager.loadUserByUsername(userEntity.getUsername());
        return AppUserConvertor.toDomain(registeredUserEntity);
    }

    public AppUser create(AppUser user) {
        validate(user);

        AppUser onboardingUser = setDefaultValuesForCreation(user);
        AuthUser userEntity = toEntity(onboardingUser);
        userDetailsManager.createUser(userEntity);
        AuthUser registeredUserEntity = (AuthUser) userDetailsManager.loadUserByUsername(userEntity.getUsername());
        return AppUserConvertor.toDomain(registeredUserEntity);
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

    private AppUser setDefaultValuesForRegistration(AppUser user) {
        return user.toBuilder()
                .username(user.username().toLowerCase())
                .password(passwordEncoder.encode(user.password()))
                .roles(Set.of(Role.AUTHENTICATED_USER))
                .enabled(false)
                .createdDate(LocalDateTime.now())
                .passwordChangedAt(LocalDateTime.now())
                .build();
    }

    private AppUser setDefaultValuesForCreation(AppUser user) {
        return user.toBuilder()
                .username(user.username().toLowerCase())
                .password(passwordEncoder.encode(user.password()))
                .enabled(true)
                .createdDate(LocalDateTime.now())
                .build();
    }

}
