package com.github.nramc.dev.journey.api.account;

import java.util.Map;

/**
 * Event triggered when user requested email code.
 */
public record EmailCodeRequestedEvent(
        String username,
        Map<String, Object> metadata
) {
}
