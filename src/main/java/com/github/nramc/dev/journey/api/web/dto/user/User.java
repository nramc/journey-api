package com.github.nramc.dev.journey.api.web.dto.user;

import com.github.nramc.dev.journey.api.security.Role;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder(toBuilder = true)
public record User(
        String username,
        String name,
        String emailAddress,
        LocalDateTime createdDate,
        LocalDateTime lastLoggedIn,
        boolean enabled,
        Set<Role> roles
) {
}
