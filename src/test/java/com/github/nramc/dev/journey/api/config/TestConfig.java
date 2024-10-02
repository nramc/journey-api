package com.github.nramc.dev.journey.api.config;

import com.github.nramc.dev.journey.api.core.user.security.Role;
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
    public static final AuthUser TEST_USER = AuthUser.builder()
            .username("arlanda_cuffkut@toddler.xbx")
            .password("test-password")
            .roles(Set.of(Role.AUTHENTICATED_USER))
            .name("USER")
            .enabled(true)
            .build();
    public static final AuthUser ADMIN_USER = AuthUser.builder()
            .username("tacoma_dominguezkt@muscle.wrp")
            .password("{noop}test-password")
            .roles(Set.of(Role.AUTHENTICATED_USER, Role.MAINTAINER, Role.ADMINISTRATOR))
            .name("Administrator")
            .enabled(true)
            .mfaEnabled(true)
            .build();
    public static final AuthUser AUTHENTICATED_USER = AuthUser.builder()
            .username("tomie_esserhhjg@reward.hc")
            .password("test")
            .roles(Set.of(Role.AUTHENTICATED_USER))
            .name("Authenticated User")
            .enabled(true)
            .build();
    public static final AuthUser GUEST_USER = AuthUser.builder()
            .username("GUEST")
            .password("test")
            .roles(Set.of(Role.GUEST_USER))
            .name("Guest")
            .enabled(true)
            .build();

    @Bean
    @Lazy
    public UserDetailsManager inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) {

        return new InMemoryUserDetailsManager(
                TEST_USER.toBuilder().password(passwordEncoder.encode(TEST_USER.getPassword())).build(),
                AUTHENTICATED_USER.toBuilder().password(passwordEncoder.encode(AUTHENTICATED_USER.getPassword())).build(),
                ADMIN_USER.toBuilder().password(passwordEncoder.encode(ADMIN_USER.getPassword())).build(),
                GUEST_USER.toBuilder().password(passwordEncoder.encode(GUEST_USER.getPassword())).build()
        ) {
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
