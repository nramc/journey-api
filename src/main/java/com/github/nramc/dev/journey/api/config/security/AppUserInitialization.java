package com.github.nramc.dev.journey.api.config.security;

import com.github.nramc.dev.journey.api.config.security.ApplicationUserProperties.ApplicationUser;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.AuthUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(ApplicationUserProperties.class)
@Profile("!workspace")
public class AppUserInitialization {
    private final AuthUserDetailsService authUserDetailsService;
    private final ApplicationUserProperties properties;
    private final PasswordEncoder passwordEncoder;

    @EventListener
    @SuppressWarnings("unused")
    void onApplicationEvent(ContextRefreshedEvent event) {
        List<ApplicationUser> users = properties.users();
        CollectionUtils.emptyIfNull(users).forEach(this::createUserIfNotExists);
    }

    private void createUserIfNotExists(ApplicationUser userProperties) {
        if (authUserDetailsService.userExists(userProperties.username())) {
            log.info("app user[{}] already exists in database.", userProperties.name());
        } else {
            UserDetails appUser = AuthUser.builder()
                    .username(userProperties.username())
                    .password(passwordEncoder.encode(userProperties.password()))
                    .roles(userProperties.roles())
                    .name(userProperties.name())
                    .enabled(true)
                    .createdDate(LocalDateTime.now())
                    .build();

            authUserDetailsService.createUser(appUser);
            log.info("app user[{}] created with configured env variable.", userProperties.name());
        }
    }
}
