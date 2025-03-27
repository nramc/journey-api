package com.github.nramc.dev.journey.api.config.security;


import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.domain.user.Role;
import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

import static com.github.nramc.dev.journey.api.config.security.WithMockGuestUser.PASSWORD;
import static com.github.nramc.dev.journey.api.config.security.WithMockGuestUser.USERNAME;
import static com.github.nramc.dev.journey.api.core.domain.user.Role.Constants.GUEST_USER;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@WithMockUser(username = USERNAME, password = PASSWORD, authorities = {GUEST_USER})
public @interface WithMockGuestUser {
    String USERNAME = "guest-user@example.com";
    String PASSWORD = "test-password";

    AppUser USER_DETAILS = AppUser.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .name("Guest User")
            .enabled(true)
            .roles(Set.of(Role.GUEST_USER))
            .mfaEnabled(false)
            .build();
}
