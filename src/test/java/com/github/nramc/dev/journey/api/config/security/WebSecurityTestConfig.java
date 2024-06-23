package com.github.nramc.dev.journey.api.config.security;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.security.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Set;

@TestConfiguration
public class WebSecurityTestConfig {

    @Bean
    @Primary
    @Qualifier("testUserDetailsService")
    public UserDetailsService testUserDetailsService(PasswordEncoder passwordEncoder) {
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
                .emailAddress("authenticated.user@gmail.com")
                .build();
        return new InMemoryUserDetailsManager(testUser, authenticatedUser, admin) {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return switch (username) {
                    case "test-admin" -> admin;
                    case "auth-user" -> authenticatedUser;
                    default -> testUser;
                };
            }
        };
    }

}