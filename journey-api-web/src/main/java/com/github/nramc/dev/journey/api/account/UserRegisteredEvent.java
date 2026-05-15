package com.github.nramc.dev.journey.api.account;

/**
 * Application event published by the {@code account} module when a new user
 * successfully completes registration.
 *
 * <p>The {@code notification} module listens to this event via
 * {@link com.github.nramc.dev.journey.api.notification.NotificationEventHandler}
 * and dispatches admin notifications without the {@code account} module
 * depending on {@code notification}.
 *
 * @param username the registered user's username (email address)
 */
public record UserRegisteredEvent(String username) {
}
