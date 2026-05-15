package com.github.nramc.dev.journey.api.shared;

import java.util.List;

/**
 * Provides a list of administrator e-mail addresses.
 *
 * <p>Defined in {@code shared} so the {@code notification} module can depend on it
 * without creating a circular dependency back to the {@code account} module.
 *
 * <p>Implemented by {@code account.repository.AuthUserDetailsService}.
 */
public interface AdminEmailProvider {

    /**
     * Returns the e-mail addresses (usernames) of all administrator accounts.
     *
     * @return non-null, possibly empty list of admin e-mail addresses
     */
    List<String> getAdminEmails();
}
