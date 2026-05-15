/**
 * Shared module — pure domain types, exceptions, utilities, validation constraints,
 * web path constants, and cross-module application events.
 *
 * <p>Declared {@code OPEN} so all its types (domain, exceptions, utils, validation, web)
 * are accessible to every other module without explicit named-interface declarations.
 * No dependency on any other feature module; all other modules depend on this one.
 */
@ApplicationModule(
        displayName = "Shared",
        type = ApplicationModule.Type.OPEN
)
package com.github.nramc.dev.journey.api.shared;

import org.springframework.modulith.ApplicationModule;
