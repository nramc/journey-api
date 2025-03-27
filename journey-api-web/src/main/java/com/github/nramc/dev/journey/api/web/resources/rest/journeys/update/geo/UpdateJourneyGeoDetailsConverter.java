package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.geo;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyGeoDetailsEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
class UpdateJourneyGeoDetailsConverter {
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
}
