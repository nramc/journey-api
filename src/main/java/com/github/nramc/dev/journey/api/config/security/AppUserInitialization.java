package com.github.nramc.dev.journey.api.config.security;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.services.AuthUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(ApplicationUserProperties.class)
public class AppUserInitialization {
    private final AuthUserDetailsService authUserDetailsService;
    private final ApplicationUserProperties properties;
    private final PasswordEncoder passwordEncoder;

    @EventListener
    @SuppressWarnings("unused")
    void onApplicationEvent(ContextRefreshedEvent event) {

        if (authUserDetailsService.userExists(properties.username())) {
            log.info("app user already exists in database.");
        } else {
            UserDetails appUser = AuthUser.builder()
                    .username(properties.username())
                    .password(passwordEncoder.encode(properties.password()))
                    .roles(properties.roles())
                    .name(properties.name())
                    .enabled(true)
                    .createdDate(LocalDateTime.now())
                    .build();

            authUserDetailsService.createUser(appUser);
            log.info("app user created with configured env variable.");
        }
    }
}
