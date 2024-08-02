package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.config.security.Role;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Set;

@TestConfiguration
public class TestConfig {

    @Bean
    @Lazy
    public UserDetailsManager inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) {
        // The builder will ensure the passwords are encoded before saving in memory
        UserDetails testUser = AuthUser.builder()
                .username("test-user")
                .password(passwordEncoder.encode("test-password"))
                .roles(Set.of(Role.AUTHENTICATED_USER))
                .name("USER")
                .build();
        UserDetails admin = AuthUser.builder()
                .username("test-admin")
                .password(passwordEncoder.encode("test-password"))
                .roles(Set.of(Role.AUTHENTICATED_USER, Role.MAINTAINER))
                .name("Administrator")
                .build();
        UserDetails authenticatedUser = AuthUser.builder()
                .username("auth-user")
                .password(passwordEncoder.encode("test"))
                .roles(Set.of(Role.AUTHENTICATED_USER))
                .name("Authenticated User")
                .build();

        return new InMemoryUserDetailsManager(testUser, authenticatedUser, admin) {
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
