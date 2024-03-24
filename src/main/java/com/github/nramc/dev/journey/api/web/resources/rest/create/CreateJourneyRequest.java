package com.github.nramc.dev.journey.api.web.resources.rest.create;

import com.github.nramc.commons.geojson.domain.GeoJson;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record CreateJourneyRequest(
        String id,
        String title,
        String description,
        String category,
        String city,
        String country,
        List<String> tags,
        String thumbnail,
        GeoJson location) {
}
