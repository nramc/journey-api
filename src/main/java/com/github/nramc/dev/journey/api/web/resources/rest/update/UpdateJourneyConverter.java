package com.github.nramc.dev.journey.api.web.resources.rest.update;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyGeoDetailsEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyMediaDetailsEntity;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class UpdateJourneyConverter {

    public static JourneyEntity copyData(UpdateJourneyBasicDetailsRequest fromRequest, JourneyEntity toEntity) {
        return toEntity.toBuilder()
                .name(fromRequest.name())
                .title(fromRequest.title())
                .description(fromRequest.description())
                .city(fromRequest.city())
                .country(fromRequest.country())
                .category(fromRequest.category())
                .tags(fromRequest.tags())
                .location(fromRequest.location())
                .thumbnail(fromRequest.thumbnail())
                .journeyDate(fromRequest.journeyDate())
                .build();

    }

    public static JourneyEntity extendWithGeoDetails(UpdateJourneyGeoDetailsRequest fromRequest, JourneyEntity toEntity) {
        JourneyExtendedEntity extendedEntity = Optional.ofNullable(toEntity.getExtended()).orElse(JourneyExtendedEntity.builder().build());

        JourneyGeoDetailsEntity geoDetailsEntity = JourneyGeoDetailsEntity.builder().geoJson(fromRequest.geoJson()).build();

        return toEntity.toBuilder()
                .extended(extendedEntity.toBuilder().geoDetails(geoDetailsEntity).build())
                .build();

    }

    public static JourneyEntity extendWithMediaDetails(UpdateJourneyMediaDetailsRequest fromRequest, JourneyEntity toEntity) {
        JourneyExtendedEntity extendedEntity = Optional.ofNullable(toEntity.getExtended()).orElse(JourneyExtendedEntity.builder().build());

        JourneyMediaDetailsEntity mediaDetailsEntity = JourneyMediaDetailsEntity.builder()
                .images(fromRequest.images())
                .videos(fromRequest.Videos())
                .build();

        return toEntity.toBuilder()
                .extended(extendedEntity.toBuilder().mediaDetails(mediaDetailsEntity).build())
                .build();
    }
}
