package com.github.nramc.dev.journey.api.web.resources.rest.auth.utils;

import com.github.nramc.dev.journey.api.core.domain.user.Role;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

    public static Set<Visibility> getVisibilityFromAuthority(Collection<? extends GrantedAuthority> authorities) {
        Set<Visibility> visibilities = new HashSet<>();
        if (isAdministratorRoleExists(authorities)) {
            visibilities.add(Visibility.ADMINISTRATOR);
        }
        if (isMaintainerRoleExists(authorities)) {
            visibilities.add(Visibility.MAINTAINER);
        }
        if (isGuestUser(authorities)) {
            visibilities.add(Visibility.GUEST);
        }

        return visibilities;
    }

}
