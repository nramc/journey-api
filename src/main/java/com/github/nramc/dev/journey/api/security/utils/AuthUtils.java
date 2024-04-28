package com.github.nramc.dev.journey.api.security.utils;

import com.github.nramc.dev.journey.api.security.Role;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@UtilityClass
public class AuthUtils {

    public static boolean isAdministratorRoleExists(Collection<? extends GrantedAuthority> authorities) {
        return CollectionUtils.emptyIfNull(authorities).stream()
                .anyMatch(authority -> StringUtils.equals(authority.getAuthority(), Role.ADMINISTRATOR.name()));
    }

    public static boolean isMaintainerRoleExists(Collection<? extends GrantedAuthority> authorities) {
        return CollectionUtils.emptyIfNull(authorities).stream()
                .anyMatch(authority -> StringUtils.equals(authority.getAuthority(), Role.MAINTAINER.name()));
    }

    public static boolean isAuthenticatedUser(Collection<? extends GrantedAuthority> authorities) {
        return CollectionUtils.emptyIfNull(authorities).stream()
                .anyMatch(authority -> StringUtils.equals(authority.getAuthority(), Role.AUTHENTICATED_USER.name()));
    }

    public static boolean isGuestUser(Collection<? extends GrantedAuthority> authorities) {
        return CollectionUtils.emptyIfNull(authorities).stream()
                .anyMatch(authority -> StringUtils.equals(authority.getAuthority(), Role.GUEST_USER.name()));
    }

}
