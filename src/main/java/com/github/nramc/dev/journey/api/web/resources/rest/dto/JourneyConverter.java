package com.github.nramc.dev.journey.api.web.resources.rest.dto;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class JourneyConverter {

    public static Journey convert(JourneyEntity entity) {
        return Journey.builder()
                .id(entity.getId())
                .name(entity.getName())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .city(entity.getCity())
                .country(entity.getCountry())
                .category(entity.getCategory())
                .tags(entity.getTags())
                .location(entity.getLocation())
                .thumbnail(entity.getThumbnail())
                .journeyDate(entity.getJourneyDate())
                .createdDate(entity.getCreatedDate())
                .extendedDetails(getExtendedDetails(entity))
                .build();
    }

    private static JourneyExtendedDetails getExtendedDetails(JourneyEntity journeyEntity) {
        return Optional.of(journeyEntity)
                .filter(journey -> journey.getExtended() != null)
                .map(journey -> JourneyExtendedDetails.builder()
                        .geoDetails(getGeoDetails(journey))
                        .mediaDetails(getMediaDetails(journey))
                        .build())
                .orElse(null);
    }

    private static JourneyGeoDetails getGeoDetails(JourneyEntity journeyEntity) {
        return Optional.ofNullable(journeyEntity.getExtended())
                .map(JourneyExtendedEntity::getGeoDetails)
                .map(journeyGeoDetailsEntity -> JourneyGeoDetails.builder()
                        .geoJson(journeyGeoDetailsEntity.getGeoJson())
                        .build())
                .orElse(null);
    }

    private static JourneyMediaDetails getMediaDetails(JourneyEntity journeyEntity) {
        return Optional.ofNullable(journeyEntity.getExtended())
                .map(JourneyExtendedEntity::getMediaDetails)
                .map(journeyMediaDetailsEntity -> JourneyMediaDetails.builder()
                        .images(journeyMediaDetailsEntity.getImages())
                        .videos(journeyMediaDetailsEntity.getVideos())
                        .build())
                .orElse(null);

    }

}
