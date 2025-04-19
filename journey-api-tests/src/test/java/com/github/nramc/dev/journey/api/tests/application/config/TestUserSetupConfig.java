package com.github.nramc.dev.journey.api.tests.application.config;

import com.github.nramc.dev.journey.api.core.domain.user.Role;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.AuthUserDetailsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.Set;

@TestConfiguration
@Profile("integration")
public class TestUserSetupConfig {
    private static final AuthUser AUTH_USER = AuthUser.builder()
            .name("Journey User")
            .username("journey-test-user@journey.com")
            .password("Journey-Test@123")
            .enabled(true)
            .roles(Set.of(Role.AUTHENTICATED_USER))
            .build();
    private static final AuthUser GUEST_USER = AuthUser.builder()
            .name("Journey Guest User")
            .username("GUEST")
            .password("Journey-Guest@123")
            .enabled(true)
            .roles(Set.of(Role.GUEST_USER))
            .build();

    @Bean
    CommandLineRunner setupTestUser(AuthUserDetailsService authUserDetailsService) {
        return args -> {
            if (!authUserDetailsService.userExists(AUTH_USER.getUsername())) {
                authUserDetailsService.createUser(AUTH_USER);
            }

            if (!authUserDetailsService.userExists(GUEST_USER.getUsername())) {
                authUserDetailsService.createUser(GUEST_USER);
            }
        };
    }

}
