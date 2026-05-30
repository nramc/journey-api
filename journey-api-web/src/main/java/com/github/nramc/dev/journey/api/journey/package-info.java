/**
 * Journey module — journey aggregate, CRUD operations, statistics, timeline,
 * Cloudinary image gateway, and REST resources for journey management.
 *
 * <p>Depends only on {@code shared}.
 */
@ApplicationModule(
        displayName = "Journey",
        allowedDependencies = {"shared"}
)
package com.github.nramc.dev.journey.api.journey;

import org.springframework.modulith.ApplicationModule;
