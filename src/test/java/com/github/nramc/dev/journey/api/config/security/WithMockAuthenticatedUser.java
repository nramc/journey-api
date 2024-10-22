package com.github.nramc.dev.journey.api.config.security;


import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.domain.user.Role;
import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

import static com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser.PASSWORD;
import static com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser.USERNAME;
import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.AUTHENTICATED_USER;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@WithMockUser(username = USERNAME, password = PASSWORD, authorities = {AUTHENTICATED_USER})
public @interface WithMockAuthenticatedUser {
    String USERNAME = "test.user@example.com";
    String PASSWORD = "test-password";

    AppUser USER_DETAILS = AppUser.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .name("Authenticated User")
            .enabled(true)
            .roles(Set.of(Role.AUTHENTICATED_USER))
            .mfaEnabled(false)
            .build();
}
