package com.github.nramc.dev.journey.api.web.resources.rest.dto;

import com.github.nramc.commons.geojson.domain.GeoJson;
import jakarta.validation.constraints.NotNull;

public record JourneyGeoDetails(
        @NotNull GeoJson geoJson) {
}
