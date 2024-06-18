package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.geo;

import com.github.nramc.commons.geojson.domain.GeoJson;
import jakarta.validation.constraints.NotNull;

public record UpdateJourneyGeoDetailsRequest(@NotNull GeoJson geoJson) {
}
