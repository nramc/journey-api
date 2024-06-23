package com.github.nramc.dev.journey.api.web.resources.rest.users;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import lombok.experimental.UtilityClass;

import java.util.Set;

import static com.github.nramc.dev.journey.api.security.Role.AUTHENTICATED_USER;

@UtilityClass
public class UsersData {
    public static final AuthUser AUTH_USER = AuthUser.builder()
            .username("test-user")
            .password("test")
            .name("Test User")
            .emailAddress("test.user@gmail.com")
            .roles(Set.of(AUTHENTICATED_USER))
            .enabled(true)
            .build();
}
