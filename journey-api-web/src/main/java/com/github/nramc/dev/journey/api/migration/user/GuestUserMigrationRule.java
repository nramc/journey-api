package com.github.nramc.dev.journey.api.migration.user;

import com.github.nramc.dev.journey.api.core.domain.user.Role;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import com.github.nramc.dev.journey.api.repository.user.AuthUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class GuestUserMigrationRule implements Runnable {
    private final AuthUserDetailsService authUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run() {
        UserDetails user = AuthUser.builder()
                .username("GUEST")
                .password(passwordEncoder.encode(""))
                .roles(Set.of(Role.GUEST_USER))
                .name("Guest")
                .enabled(true)
                .createdDate(LocalDateTime.now())
                .build();

        if (authUserDetailsService.userExists(user.getUsername())) {
            log.info("app user[{}] already exists in database.", user.getUsername());
        } else {
            authUserDetailsService.createUser(user);
            log.info("app user[{}] created", user.getUsername());
        }
    }
}
