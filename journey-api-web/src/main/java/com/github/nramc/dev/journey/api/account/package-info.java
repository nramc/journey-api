/**
 * Account module — user registration, authentication (JWT, TOTP, WebAuthn, email codes),
 * and all user/account management REST resources.
 *
 * <p>Depends on {@code shared} (domain types, utilities, events) and {@code infrastructure}
 * (JWT properties, application properties, security config).
 * Cross-module notifications are published as application events and consumed by the
 * {@code notification} module — no direct dependency on it.
 */
@ApplicationModule(
        displayName = "Account",
        allowedDependencies = {"shared", "infrastructure"}
)
package com.github.nramc.dev.journey.api.account;

import org.springframework.modulith.ApplicationModule;
