package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.geo;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyGeoDetailsEntity;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
class UpdateJourneyGeoDetailsConverter {
    static JourneyEntity extendWithGeoDetails(UpdateJourneyGeoDetailsRequest fromRequest, JourneyEntity toEntity) {
        JourneyExtendedEntity extendedEntity = Optional.ofNullable(toEntity.getExtended()).orElse(JourneyExtendedEntity.builder().build());

        JourneyGeoDetailsEntity geoDetailsEntity = JourneyGeoDetailsEntity.builder()
                .title(fromRequest.title())
                .city(fromRequest.city())
                .country(fromRequest.country())
                .location(fromRequest.location())
                .geoJson(fromRequest.geoJson())
                .build();

        return toEntity.toBuilder()
                .extended(extendedEntity.toBuilder().geoDetails(geoDetailsEntity).build())
                .build();

    }
}
