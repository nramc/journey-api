package com.github.nramc.dev.journey.api.web.dto.converter;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyVideoDetailEntity;
import com.github.nramc.dev.journey.api.web.dto.Journey;
import com.github.nramc.dev.journey.api.web.dto.JourneyImagesDetails;
import com.github.nramc.dev.journey.api.web.dto.JourneyVideoDetail;
import com.github.nramc.dev.journey.api.web.dto.JourneyVideosDetails;
import com.github.nramc.dev.journey.api.web.dto.JourneyExtendedDetails;
import com.github.nramc.dev.journey.api.web.dto.JourneyGeoDetails;
import com.github.nramc.dev.journey.api.web.dto.JourneyImageDetail;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

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
                .isPublished(BooleanUtils.toBoolean(entity.getIsPublished()))
                .build();
    }

    private static JourneyExtendedDetails getExtendedDetails(JourneyEntity journeyEntity) {
        return Optional.of(journeyEntity)
                .filter(journey -> journey.getExtended() != null)
                .map(journey -> JourneyExtendedDetails.builder()
                        .geoDetails(getGeoDetails(journey))
                        .imagesDetails(getImagesDetails(journey))
                        .videosDetails(getVideosDetails(journey))
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
                .map(entity -> JourneyImagesDetails.builder().images(toImageDetails(entity.getImages())).build())
                .orElse(null);
    }

    private static List<JourneyImageDetail> toImageDetails(List<JourneyImageDetailEntity> entities) {
        return CollectionUtils.emptyIfNull(entities).stream().map(entity -> JourneyImageDetail.builder()
                .url(entity.getUrl())
                .assetId(entity.getAssetId())
                .build()
        ).toList();
    }

    private static JourneyVideosDetails getVideosDetails(JourneyEntity journeyEntity) {
        return Optional.ofNullable(journeyEntity.getExtended())
                .map(JourneyExtendedEntity::getVideosDetails)
                .map(entity -> JourneyVideosDetails.builder().videos(toVideoDetails(entity.getVideos())).build())
                .orElse(null);
    }

    private static List<JourneyVideoDetail> toVideoDetails(List<JourneyVideoDetailEntity> entities) {
        return CollectionUtils.emptyIfNull(entities).stream().map(entity -> JourneyVideoDetail.builder()
                .videoId(entity.getVideoId())
                .build()
        ).toList();
    }

}
