package com.github.nramc.dev.journey.api.web.dto.user;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@UtilityClass
public class UserConverter {

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
                .emailAddress(authUser.getEmailAddress())
                .lastLoggedIn(authUser.getLastLoggedIn())
                .createdDate(authUser.getCreatedDate())
                .enabled(authUser.isEnabled())
                .build();
    }

}
