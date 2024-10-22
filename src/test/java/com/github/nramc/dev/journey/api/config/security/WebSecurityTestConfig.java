package com.github.nramc.dev.journey.api.config.security;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@TestConfiguration
public class WebSecurityTestConfig {

    @Bean
    @Primary
    @Qualifier("testUserDetailsService")
    public UserDetailsManager testUserDetailsService(PasswordEncoder passwordEncoder) {
        // The builder will ensure the passwords are encoded before saving in memory
        UserDetails authenticatedUser = AuthUser.builder()
                .username(WithMockAuthenticatedUser.USERNAME)
                .password(passwordEncoder.encode(WithMockAuthenticatedUser.PASSWORD))
                .roles(WithMockAuthenticatedUser.USER_DETAILS.roles())
                .name(WithMockAuthenticatedUser.USER_DETAILS.name())
                .build();
        UserDetails administratorUser = AuthUser.builder()
                .username(WithMockAdministratorUser.USERNAME)
                .password(passwordEncoder.encode(WithMockAdministratorUser.PASSWORD))
                .roles(WithMockAdministratorUser.USER_DETAILS.roles())
                .name(WithMockAdministratorUser.USER_DETAILS.name())
                .build();
        UserDetails maintainerUser = AuthUser.builder()
                .username(WithMockMaintainerUser.USERNAME)
                .password(passwordEncoder.encode(WithMockMaintainerUser.PASSWORD))
                .roles(WithMockMaintainerUser.USER_DETAILS.roles())
                .name(WithMockMaintainerUser.USER_DETAILS.name())
                .build();

        return new InMemoryUserDetailsManager(authenticatedUser, administratorUser, maintainerUser) {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return switch (username) {
                    case WithMockAdministratorUser.USERNAME -> administratorUser;
                    case WithMockMaintainerUser.USERNAME -> maintainerUser;
                    default -> authenticatedUser;
                };
            }
        };
    }

}
