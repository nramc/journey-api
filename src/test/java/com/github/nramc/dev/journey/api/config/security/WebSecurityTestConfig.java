package com.github.nramc.dev.journey.api.config.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class WebSecurityTestConfig {

    @Bean
    @Primary
    public UserDetailsService testUserDetailsService(PasswordEncoder passwordEncoder) {
        // The builder will ensure the passwords are encoded before saving in memory
        UserDetails user = User.builder()
                .username("test-user")
                .password(passwordEncoder.encode("test-password"))
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("test-admin")
                .password(passwordEncoder.encode("test-password"))
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

}