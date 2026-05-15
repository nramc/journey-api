/**
 * Infrastructure module — cross-cutting technical concerns:
 * security configuration, MongoDB setup, OpenAPI docs, actuator, timezone, migration.
 *
 * <p>Declared {@code OPEN} so all its types (configs, helpers) are accessible to every
 * other module without an explicit {@code allowedDependencies} declaration.
 */
@ApplicationModule(
        displayName = "Infrastructure",
        type = ApplicationModule.Type.OPEN
)
package com.github.nramc.dev.journey.api.infrastructure;

import org.springframework.modulith.ApplicationModule;
