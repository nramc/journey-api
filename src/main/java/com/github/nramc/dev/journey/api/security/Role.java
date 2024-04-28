package com.github.nramc.dev.journey.api.security;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Set;

public enum Role {
    ADMINISTRATOR,
    AUTHENTICATED_USER,
    MAINTAINER,
    GUEST_USER;

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
