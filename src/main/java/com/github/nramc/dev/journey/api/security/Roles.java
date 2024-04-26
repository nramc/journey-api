package com.github.nramc.dev.journey.api.security;

import lombok.experimental.UtilityClass;

public enum Roles {
    ADMINISTRATOR,
    AUTHENTICATED_USER,
    MAINTAINER,
    GUEST;

    @UtilityClass
    public static class Constants {
        public static final String ADMINISTRATOR = "ADMINISTRATOR";
        public static final String AUTHENTICATED_USER = "AUTHENTICATED_USER";
        public static final String MAINTAINER = "MAINTAINER";
        public static final String GUEST = "GUEST";
    }
}
