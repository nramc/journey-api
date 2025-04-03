package com.github.nramc.dev.journey.api.web.resources.rest.auth.utils;

import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.domain.user.Role;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (isAuthenticatedUser(authorities)) {
            visibilities.add(Visibility.AUTHENTICATED_USER);
        }
        if (isGuestUser(authorities)) {
            visibilities.add(Visibility.GUEST);
        }

        return visibilities;
    }

    public static Set<Role> getRoleFromAuthority(Collection<? extends GrantedAuthority> authorities) {
        Set<Role> roles = new HashSet<>();
        if (isAdministratorRoleExists(authorities)) {
            roles.add(Role.ADMINISTRATOR);
        }
        if (isMaintainerRoleExists(authorities)) {
            roles.add(Role.MAINTAINER);
        }
        if (isAuthenticatedUser(authorities)) {
            roles.add(Role.AUTHENTICATED_USER);
        }
        if (isGuestUser(authorities)) {
            roles.add(Role.GUEST_USER);
        }

        return roles;
    }

    public static Set<Visibility> getVisibilityFromRole(Collection<Role> roles) {
        return CollectionUtils.emptyIfNull(roles).stream().map(AuthUtils::toVisibility).collect(Collectors.toSet());
    }

    public static AppUser toAppUser(Authentication authentication) {
        return AppUser.builder()
                .username(authentication.getName())
                .roles(getRoleFromAuthority(authentication.getAuthorities()))
                .build();
    }

    private static Visibility toVisibility(Role role) {
        return switch (role) {
            case ADMINISTRATOR -> Visibility.ADMINISTRATOR;
            case MAINTAINER -> Visibility.MAINTAINER;
            case AUTHENTICATED_USER -> Visibility.AUTHENTICATED_USER;
            case GUEST_USER -> Visibility.GUEST;
        };

    }

    private AuthUtils() {
        throw new IllegalStateException("Utility class");
    }

}
