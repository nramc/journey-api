package com.github.nramc.dev.journey.api.config.security;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Set;

import static com.github.nramc.dev.journey.api.web.resources.rest.users.UsersData.MFA_USER;

@TestConfiguration
public class WebSecurityTestConfig {

    @Bean
    @Primary
    @Qualifier("testUserDetailsService")
    public UserDetailsManager testUserDetailsService(PasswordEncoder passwordEncoder) {
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
        UserDetails maintainer = AuthUser.builder()
                .username("test-maintainer")
                .password(passwordEncoder.encode("test-password"))
                .roles(Set.of(Role.AUTHENTICATED_USER, Role.MAINTAINER))
                .name("Maintainer")
                .build();
        UserDetails authenticatedUser = AuthUser.builder()
                .username("auth-user")
                .password(passwordEncoder.encode("test"))
                .roles(Set.of(Role.AUTHENTICATED_USER))
                .name("Authenticated User")
                .build();

        return new InMemoryUserDetailsManager(testUser, authenticatedUser, admin, maintainer) {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return switch (username) {
                    case "test-admin" -> admin;
                    case "auth-user" -> authenticatedUser;
                    case "mfa-user" -> MFA_USER;
                    case "test-maintainer" -> maintainer;
                    default -> testUser;
                };
            }
        };
    }

}