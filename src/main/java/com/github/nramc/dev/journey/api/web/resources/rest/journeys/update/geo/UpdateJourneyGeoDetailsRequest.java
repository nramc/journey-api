package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.geo;

import com.github.nramc.geojson.domain.GeoJson;
import com.github.nramc.geojson.domain.Geometry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

record UpdateJourneyGeoDetailsRequest(
        @NotBlank String title,
        @NotBlank String city,
        @NotBlank String country,
        @NotBlank String category,
        @NotNull Geometry location,
        @NotNull GeoJson geoJson
) {
}
