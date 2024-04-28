package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyGeoDetailsEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImagesDetailsEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyVideoDetailEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyVideosDetailsEntity;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.basic.UpdateJourneyBasicDetailsRequest;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.videos.UpdateJourneyVideosDetailsRequest;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.geo.UpdateJourneyGeoDetailsRequest;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.images.UpdateJourneyImagesDetailsRequest;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.publish.PublishJourneyRequest;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public static JourneyEntity extendWithImagesDetails(UpdateJourneyImagesDetailsRequest fromRequest, JourneyEntity toEntity) {
        JourneyExtendedEntity extendedEntity = Optional.ofNullable(toEntity.getExtended()).orElse(JourneyExtendedEntity.builder().build());

        List<JourneyImageDetailEntity> imageDetailEntities = CollectionUtils.emptyIfNull(fromRequest.images()).stream()
                .map(imageDetail -> JourneyImageDetailEntity.builder()
                        .url(imageDetail.url())
                        .assetId(imageDetail.assetId())
                        .build()
                )
                .toList();

        JourneyImagesDetailsEntity imageDetailsEntity = JourneyImagesDetailsEntity.builder()
                .images(imageDetailEntities)
                .build();

        return toEntity.toBuilder()
                .extended(extendedEntity.toBuilder().imagesDetails(imageDetailsEntity).build())
                .build();
    }

    public static JourneyEntity extendWithVideosDetails(UpdateJourneyVideosDetailsRequest fromRequest, JourneyEntity toEntity) {
        JourneyExtendedEntity extendedEntity = Optional.ofNullable(toEntity.getExtended()).orElse(JourneyExtendedEntity.builder().build());

        List<JourneyVideoDetailEntity> videoDetailEntities = CollectionUtils.emptyIfNull(fromRequest.videos()).stream()
                .map(videoDetail -> JourneyVideoDetailEntity.builder()
                        .videoId(videoDetail.videoId())
                        .build()
                )
                .toList();

        JourneyVideosDetailsEntity journeyVideosDetailsEntity = JourneyVideosDetailsEntity.builder()
                .videos(videoDetailEntities)
                .build();

        return toEntity.toBuilder()
                .extended(extendedEntity.toBuilder().videosDetails(journeyVideosDetailsEntity).build())
                .build();
    }

    public static JourneyEntity extendEntityWith(PublishJourneyRequest request, JourneyEntity entity) {
        return entity.toBuilder()
                .visibilities(Set.copyOf(request.visibilities()))
                .isPublished(request.isPublished())
                .thumbnail(request.thumbnail())
                .build();
    }
}
