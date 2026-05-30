package com.github.nramc.dev.journey.api.account;

/**
 * Application event published by the {@code account} module when a user
 * successfully activates (confirms) their account.
 *
 * @param username the activated user's username
 */
public record AccountActivatedEvent(String username) {
}
