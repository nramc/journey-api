package com.github.nramc.dev.journey.api.config.security;

import com.github.nramc.dev.journey.api.services.AuthUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(ApplicationUserProperties.class)
public class AppUserInitialization {
    private final AuthUserDetailsService authUserDetailsService;
    private final ApplicationUserProperties properties;
    private final PasswordEncoder passwordEncoder;

    @EventListener
    void onApplicationEvent(ContextRefreshedEvent event) {

        if (authUserDetailsService.userExists(properties.username())) {
            log.info("app user already exists in database.");
        } else {
            UserDetails appUser = User.builder()
                    .username(properties.username())
                    .password(passwordEncoder.encode(properties.password()))
                    .authorities(properties.roles().toArray(String[]::new))
                    .build();

            authUserDetailsService.createUser(appUser);
            log.info("app user created with configured env variable.");
        }
    }
}
