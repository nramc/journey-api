package com.github.nramc.dev.journey.api.core.user.converters;

import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AppUserConvertor {

    public static AuthUser toEntity(AppUser appUser) {
        return AuthUser.builder()
                .username(appUser.username())
                .password(appUser.password())
                .name(appUser.name())
                .roles(appUser.roles())
                .enabled(appUser.enabled())
                .mfaEnabled(appUser.mfaEnabled())
                .createdDate(appUser.createdDate())
                .passwordChangedAt(appUser.passwordChangedAt())
                .build();
    }

    public static AppUser toDomain(AuthUser authUser) {
        return AppUser.builder()
                .username(authUser.getUsername())
                //.password(authUser.getPassword()) // Intentionally commented out to avoid sending password to client
                .name(authUser.getName())
                .roles(authUser.getRoles())
                .enabled(authUser.isEnabled())
                .mfaEnabled(authUser.isMfaEnabled())
                .createdDate(authUser.getCreatedDate())
                .passwordChangedAt(authUser.getPasswordChangedAt())
                .build();
    }

}
