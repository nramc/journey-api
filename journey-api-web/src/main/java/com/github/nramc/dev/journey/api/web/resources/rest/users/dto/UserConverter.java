package com.github.nramc.dev.journey.api.web.resources.rest.users.dto;

import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public final class UserConverter {

    public static List<User> toUsers(List<AuthUser> authUsers) {
        return CollectionUtils.emptyIfNull(authUsers)
                .stream().map(UserConverter::toUser)
                .toList();
    }

    public static User toUser(AuthUser authUser) {
        return User.builder()
                .name(authUser.getName())
                .username(authUser.getUsername())
                .roles(authUser.getRoles())
                .passwordChangedAt(authUser.getPasswordChangedAt())
                .createdDate(authUser.getCreatedDate())
                .enabled(authUser.isEnabled())
                .mfaEnabled(authUser.isMfaEnabled())
                .build();
    }

    private UserConverter() {
        throw new IllegalStateException("Utility class");
    }

}
