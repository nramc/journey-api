package com.github.nramc.dev.journey.api.security.utils;

import com.github.nramc.dev.journey.api.security.Role;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@UtilityClass
public class AuthUtils {

    public static boolean isAdministratorRoleExists(Collection<? extends GrantedAuthority> authorities) {
        return CollectionUtils.emptyIfNull(authorities).stream()
                .anyMatch(authority -> Role.ADMINISTRATOR.allVariant().contains(authority.getAuthority()));
    }

    public static boolean isMaintainerRoleExists(Collection<? extends GrantedAuthority> authorities) {
        return CollectionUtils.emptyIfNull(authorities).stream()
                .anyMatch(authority -> Role.MAINTAINER.allVariant().contains(authority.getAuthority()));
    }

    public static boolean isAuthenticatedUser(Collection<? extends GrantedAuthority> authorities) {
        return CollectionUtils.emptyIfNull(authorities).stream()
                .anyMatch(authority -> Role.AUTHENTICATED_USER.allVariant().contains(authority.getAuthority()));
    }

    public static boolean isGuestUser(Collection<? extends GrantedAuthority> authorities) {
        return CollectionUtils.emptyIfNull(authorities).stream()
                .anyMatch(authority -> Role.GUEST_USER.allVariant().contains(authority.getAuthority()));
    }

}
