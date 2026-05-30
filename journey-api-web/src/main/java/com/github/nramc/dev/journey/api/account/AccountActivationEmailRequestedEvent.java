package com.github.nramc.dev.journey.api.account;

/**
 * Application event published by the {@code account} module when an account
 * activation e-mail needs to be sent to a newly registered user.
 *
 * <p>Carrying the fully-built activation URL avoids the {@code notification} module
 * needing knowledge of URL construction, token generation, or
 * {@code ApplicationProperties}.
 *
 * @param username      registered user's username (e-mail address — the recipient)
 * @param name          user's display name (used in the e-mail body greeting)
 * @param activationUrl fully-formed account-activation deep-link for the SPA
 */
public record AccountActivationEmailRequestedEvent(
        String username,
        String name,
        String activationUrl) {
}
