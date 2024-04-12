package com.github.nramc.dev.journey.api.web.resources.rest.update.geo;

import com.github.nramc.commons.geojson.domain.Geometry;
import jakarta.validation.constraints.NotNull;

public record UpdateJourneyGeoDetailsRequest(@NotNull Geometry geoJson) {
}
