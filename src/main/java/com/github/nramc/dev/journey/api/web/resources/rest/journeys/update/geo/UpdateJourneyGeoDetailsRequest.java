package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.geo;

import com.github.nramc.commons.geojson.domain.GeoJson;
import com.github.nramc.commons.geojson.domain.Geometry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateJourneyGeoDetailsRequest(
        @NotBlank String title,
        @NotBlank String city,
        @NotBlank String country,
        @NotNull Geometry location,
        @NotNull GeoJson geoJson
) {
}
