package com.github.nramc.dev.journey.api.config.security;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.security.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
        UserDetails user = AuthUser.builder()
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
        return new InMemoryUserDetailsManager(user, admin);
    }

}