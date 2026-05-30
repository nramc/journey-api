package com.github.nramc.dev.journey.api.account.web.users.dto;

import com.github.nramc.dev.journey.api.shared.domain.user.Role;
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
