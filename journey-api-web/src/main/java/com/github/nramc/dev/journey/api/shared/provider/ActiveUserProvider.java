package com.github.nramc.dev.journey.api.shared.provider;

import com.github.nramc.dev.journey.api.shared.domain.EmailAddress;
import com.github.nramc.dev.journey.api.shared.domain.user.security.Role;

import java.util.List;
import java.util.Set;

/**
 * Provides e-mail addresses of active (enabled) users.
 */
public interface ActiveUserProvider {

    /**
     * Returns all active users existing in system.
     *
     * @return non-null, possibly empty list of active users eligible for notifications
     */
    List<ActiveUser> getActiveUsers();

    /**
     * Minimal recipient projection used across module boundaries.
     */
    record ActiveUser(EmailAddress emailAddress, String displayName, Set<Role> roles) {
    }
}


