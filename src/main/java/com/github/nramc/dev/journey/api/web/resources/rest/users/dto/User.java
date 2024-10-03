package com.github.nramc.dev.journey.api.web.resources.rest.users.dto;

import com.github.nramc.dev.journey.api.core.domain.user.Role;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder(toBuilder = true)
public record User(
        String username,
        String name,
        LocalDateTime createdDate,
        LocalDateTime passwordChangedAt,
        boolean enabled,
        Set<Role> roles,
        boolean mfaEnabled
) {
}
