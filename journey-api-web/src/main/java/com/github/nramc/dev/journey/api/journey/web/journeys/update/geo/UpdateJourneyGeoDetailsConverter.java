package com.github.nramc.dev.journey.api.journey.web.journeys.update.geo;

import com.github.nramc.dev.journey.api.journey.repository.JourneyEntity;
import com.github.nramc.dev.journey.api.journey.repository.JourneyGeoDetailsEntity;

final class UpdateJourneyGeoDetailsConverter {
    static JourneyEntity extendWithGeoDetails(UpdateJourneyGeoDetailsRequest fromRequest, JourneyEntity toEntity) {
        JourneyGeoDetailsEntity geoDetailsEntity = JourneyGeoDetailsEntity.builder()
                .title(fromRequest.title())
                .city(fromRequest.city())
                .country(fromRequest.country())
                .category(fromRequest.category())
                .location(fromRequest.location())
                .geoJson(fromRequest.geoJson())
                .build();

        return toEntity.toBuilder()
                .geoDetails(geoDetailsEntity)
                .build();
    }

    private UpdateJourneyGeoDetailsConverter() {
        throw new IllegalStateException("Utility class");
    }
}
