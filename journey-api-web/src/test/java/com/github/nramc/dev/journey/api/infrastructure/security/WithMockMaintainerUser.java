package com.github.nramc.dev.journey.api.infrastructure.security;

import com.github.nramc.dev.journey.api.shared.domain.AppUser;
import com.github.nramc.dev.journey.api.shared.domain.user.Role;
import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

import static com.github.nramc.dev.journey.api.infrastructure.security.WithMockMaintainerUser.PASSWORD;
import static com.github.nramc.dev.journey.api.infrastructure.security.WithMockMaintainerUser.USERNAME;
import static com.github.nramc.dev.journey.api.shared.domain.user.Role.Constants.AUTHENTICATED_USER;
import static com.github.nramc.dev.journey.api.shared.domain.user.Role.Constants.MAINTAINER;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@WithMockUser(username = USERNAME, password = PASSWORD, authorities = {AUTHENTICATED_USER, MAINTAINER})
public @interface WithMockMaintainerUser {
    String USERNAME = "test-maintainer@example.com";
    String PASSWORD = "test-password";

    AppUser USER_DETAILS = AppUser.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .name("Maintainer User")
            .enabled(true)
            .roles(Set.of(Role.AUTHENTICATED_USER, Role.MAINTAINER))
            .mfaEnabled(false)
            .build();
}
