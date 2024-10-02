package com.github.nramc.dev.journey.api.config.security;

import com.github.nramc.dev.journey.api.core.user.security.Role;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Set;

@ConfigurationProperties("app.operational")
public record ApplicationUserProperties(
        List<ApplicationUser> users
) {
    public record ApplicationUser(
            String username,
            String password,
            Set<Role> roles,
            String name
    ) {
        @Override
        public String toString() {
            return "ApplicationUser{" + "name='" + name + '\'' + '}';
        }
    }
}
