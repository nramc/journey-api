package com.github.nramc.dev.journey.api.core.user.security;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Set;

public enum Role {
    ADMINISTRATOR,
    AUTHENTICATED_USER,
    MAINTAINER,
    GUEST_USER;

    public List<String> allVariant() {
        return List.of(this.name(), "SCOPE_" + this.name(), "ROLE_" + this.name());
    }

    @UtilityClass
    public static class Constants {
        public static final String ADMINISTRATOR = "ADMINISTRATOR";
        public static final String AUTHENTICATED_USER = "AUTHENTICATED_USER";
        public static final String MAINTAINER = "MAINTAINER";
        public static final String GUEST_USER = "GUEST_USER";
    }

    public static List<String> toStringRoles(Set<Role> roles) {
        return CollectionUtils.emptyIfNull(roles).stream().map(Enum::name).toList();
    }
}
