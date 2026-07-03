package com.github.nramc.dev.journey.api.account;

/**
 * Application event published by the {@code account} module when a user requests
 * account/password recovery via a One-Time-Token (OTT) email link.
 *
 * <p>Carrying the fully-built recovery URL avoids the {@code notification} module
 * needing knowledge of URL construction, token generation, or {@code ApplicationProperties}.
 *
 * @param username    registered user's username (e-mail address — the recipient)
 * @param name        user's display name (used in the e-mail body greeting)
 * @param recoveryUrl fully-formed one-time-token recovery deep-link for the SPA
 */
public record PasswordRecoveryRequestedEvent(
        String username,
        String name,
        String recoveryUrl) {
}

