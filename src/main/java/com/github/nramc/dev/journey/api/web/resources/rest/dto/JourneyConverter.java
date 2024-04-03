package com.github.nramc.dev.journey.api.web.resources.rest.dto;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
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
                        .imagesDetails(getImagesDetails(journey))
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

    private static JourneyImagesDetails getImagesDetails(JourneyEntity journeyEntity) {
        return Optional.ofNullable(journeyEntity.getExtended())
                .map(JourneyExtendedEntity::getImagesDetails)
                .map(entity -> JourneyImagesDetails.builder().images(getImageDetails(entity.getImages())).build())
                .orElse(null);
    }

    private static List<JourneyImageDetail> getImageDetails(List<JourneyImageDetailEntity> entities) {
        return CollectionUtils.emptyIfNull(entities).stream().map(entity -> JourneyImageDetail.builder()
                .url(entity.getUrl())
                .assetId(entity.getAssetId())
                .build()
        ).toList();
    }

}
