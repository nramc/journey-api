package com.github.nramc.dev.journey.api.security.utils;

import com.github.nramc.dev.journey.api.security.Roles;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@UtilityClass
public class AuthUtils {

    public static boolean isAdministratorRoleExists(Collection<? extends GrantedAuthority> authorities) {
        return CollectionUtils.emptyIfNull(authorities).stream()
                .anyMatch(authority -> StringUtils.equals(authority.getAuthority(), Roles.ADMINISTRATOR.name()));
    }

    public static boolean isMaintainerRoleExists(Collection<? extends GrantedAuthority> authorities) {
        return CollectionUtils.emptyIfNull(authorities).stream()
                .anyMatch(authority -> StringUtils.equals(authority.getAuthority(), Roles.MAINTAINER.name()));
    }

    public static boolean isAuthenticatedUser(Collection<? extends GrantedAuthority> authorities) {
        return CollectionUtils.emptyIfNull(authorities).stream()
                .anyMatch(authority -> StringUtils.equals(authority.getAuthority(), Roles.AUTHENTICATED_USER.name()));
    }

    public static boolean isGuestUser(Collection<? extends GrantedAuthority> authorities) {
        return CollectionUtils.emptyIfNull(authorities).stream()
                .anyMatch(authority -> StringUtils.equals(authority.getAuthority(), Roles.GUEST.name()));
    }

}
