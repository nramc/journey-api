package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.config.security.Role;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Set;

@TestConfiguration
public class TestConfig {
    public static final AuthUser TEST_USER = AuthUser.builder()
            .username("test-user")
            .password("test-password")
            .roles(Set.of(Role.AUTHENTICATED_USER))
            .name("USER")
            .build();
    public static final AuthUser ADMIN_USER = AuthUser.builder()
            .username("test-admin")
            .password("test-password")
            .roles(Set.of(Role.AUTHENTICATED_USER, Role.MAINTAINER))
            .name("Administrator")
            .build();
    public static final AuthUser AUTHENTICATED_USER = AuthUser.builder()
            .username("auth-user")
            .password("test")
            .roles(Set.of(Role.AUTHENTICATED_USER))
            .name("Authenticated User")
            .build();
    public static final AuthUser GUEST_USER = AuthUser.builder()
            .username("GUEST")
            .password("test")
            .roles(Set.of(Role.GUEST_USER))
            .name("Guest")
            .build();

    @Bean
    @Lazy
    public UserDetailsManager inMemoryUserDetailsManager() {

        return new InMemoryUserDetailsManager(TEST_USER, AUTHENTICATED_USER, ADMIN_USER, GUEST_USER) {
            @Override
            @SuppressWarnings("unchecked")
            public AuthUser loadUserByUsername(String username) throws UsernameNotFoundException {
                Map<String, UserDetails> users = (Map<String, UserDetails>) ReflectionTestUtils.getField(this, "users");
                assert users != null;
                return (AuthUser) ReflectionTestUtils.getField(users.get(username.toLowerCase()), "delegate");
            }
        };
    }
}
