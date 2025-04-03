package com.github.nramc.dev.journey.api.repository.journey.converter;

import com.github.nramc.dev.journey.api.core.journey.Journey;
import com.github.nramc.dev.journey.api.core.journey.JourneyGeoDetails;
import com.github.nramc.dev.journey.api.core.journey.JourneyImageDetail;
import com.github.nramc.dev.journey.api.core.journey.JourneyImagesDetails;
import com.github.nramc.dev.journey.api.core.journey.JourneyVideoDetail;
import com.github.nramc.dev.journey.api.core.journey.JourneyVideosDetails;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyVideoDetailEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Optional;

public class JourneyConverter {

    public static Journey convert(JourneyEntity entity) {
        return Journey.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .tags(entity.getTags())
                .thumbnail(entity.getThumbnail())
                .journeyDate(entity.getJourneyDate())
                .createdDate(entity.getCreatedDate())
                .geoDetails(getGeoDetails(entity))
                .imagesDetails(getImagesDetails(entity))
                .videosDetails(getVideosDetails(entity))
                .isPublished(BooleanUtils.toBoolean(entity.getIsPublished()))
                .visibilities(entity.getVisibilities())
                .build();
    }

    private static JourneyGeoDetails getGeoDetails(JourneyEntity journeyEntity) {
        return Optional.ofNullable(journeyEntity.getGeoDetails())
                .map(journeyGeoDetailsEntity -> JourneyGeoDetails.builder()
                        .title(journeyGeoDetailsEntity.getTitle())
                        .city(journeyGeoDetailsEntity.getCity())
                        .country(journeyGeoDetailsEntity.getCountry())
                        .category(journeyGeoDetailsEntity.getCategory())
                        .location(journeyGeoDetailsEntity.getLocation())
                        .geoJson(journeyGeoDetailsEntity.getGeoJson())
                        .build())
                .orElse(null);
    }

    private static JourneyImagesDetails getImagesDetails(JourneyEntity journeyEntity) {
        return Optional.ofNullable(journeyEntity.getImagesDetails())
                .map(entity -> JourneyImagesDetails.builder().images(toImageDetails(entity.getImages())).build())
                .orElse(null);
    }

    private static List<JourneyImageDetail> toImageDetails(List<JourneyImageDetailEntity> entities) {
        return CollectionUtils.emptyIfNull(entities).stream().map(entity -> JourneyImageDetail.builder()
                .url(entity.getUrl())
                .assetId(entity.getAssetId())
                .publicId(entity.getPublicId())
                .title(entity.getTitle())
                .isFavorite(entity.isFavorite())
                .isThumbnail(entity.isThumbnail())
                .eventDate(entity.getEventDate())
                .build()
        ).toList();
    }

    private static JourneyVideosDetails getVideosDetails(JourneyEntity journeyEntity) {
        return Optional.ofNullable(journeyEntity.getVideosDetails())
                .map(entity -> JourneyVideosDetails.builder().videos(toVideoDetails(entity.getVideos())).build())
                .orElse(null);
    }

    private static List<JourneyVideoDetail> toVideoDetails(List<JourneyVideoDetailEntity> entities) {
        return CollectionUtils.emptyIfNull(entities).stream().map(entity -> JourneyVideoDetail.builder()
                .videoId(entity.getVideoId())
                .build()
        ).toList();
    }

    private JourneyConverter() {
        throw new IllegalStateException("Utility class");
    }

}
