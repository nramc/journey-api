package com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImagesDetailsEntity;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.TimelineData;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@UtilityClass
public class TimelineDataTransformer {
    private static final int MAX_IMAGES_PER_JOURNEY = 3;
    public static final String DEFAULT_HEADING = "Timeline";

    @SuppressWarnings("java:S107")
    public static TimelineData transform(List<JourneyEntity> entities,
                                         List<String> journeyIDs,
                                         List<String> cities,
                                         List<String> countries,
                                         List<String> categories,
                                         List<Long> years,
                                         Boolean today,
                                         Boolean upcoming) {
        if (Boolean.TRUE.equals(today)) {
            return TodayTimelineTransformer.transform(entities);
        } else if (Boolean.TRUE.equals(upcoming)) {
            return UpcomingTimelineTransformer.transform(entities);
        } else if (CollectionUtils.isNotEmpty(years)) {
            return YearTimelineTransformer.transform(entities, years);
        } else if (CollectionUtils.isNotEmpty(journeyIDs)) {
            return JourneyTimelineTransformer.transform(entities);
        } else if (CollectionUtils.isNotEmpty(cities)) {
            return CityTimelineTransformer.transform(entities);
        } else if (CollectionUtils.isNotEmpty(countries)) {
            return CountryTimelineTransformer.transform(entities);
        } else if (CollectionUtils.isNotEmpty(categories)) {
            return CategoryTimelineTransformer.transform(entities);
        } else {
            return TimelineData.builder()
                    .heading(DEFAULT_HEADING)
                    .images(getImagesForTimeline(entities))
                    .build();
        }
    }

    private static List<TimelineData.TimelineImage> getImagesForTimeline(List<JourneyEntity> entities) {
        return CollectionUtils.emptyIfNull(entities).stream()
                .map(TimelineDataTransformer::getImages)
                .flatMap(Collection::stream)
                .map(TimelineDataTransformer::toTimelineImage)
                .toList();
    }

    private static TimelineData.TimelineImage toTimelineImage(JourneyImageDetailEntity entity) {
        return TimelineData.TimelineImage.builder()
                .src(entity.getUrl())
                .caption(entity.getTitle())
                .args(Map.of())
                .build();
    }

    public static List<JourneyImageDetailEntity> getImages(JourneyEntity journeyEntity) {
        List<JourneyImageDetailEntity> favoriteImages = getFavoriteImages(journeyEntity);
        return favoriteImages.isEmpty() ? getFirstNImages(journeyEntity, MAX_IMAGES_PER_JOURNEY) : favoriteImages;
    }

    private static List<JourneyImageDetailEntity> getFavoriteImages(JourneyEntity journeyEntity) {
        return Optional.of(journeyEntity)
                .map(JourneyEntity::getExtended)
                .map(JourneyExtendedEntity::getImagesDetails)
                .map(JourneyImagesDetailsEntity::getImages)
                .filter(CollectionUtils::isNotEmpty)
                .orElse(List.of())
                .stream()
                .filter(JourneyImageDetailEntity::isFavorite)
                .limit(MAX_IMAGES_PER_JOURNEY)
                .toList();
    }

    public static List<JourneyImageDetailEntity> getFirstNImages(JourneyEntity journeyEntity, int maxImagesPerJourney) {
        return Optional.of(journeyEntity)
                .map(JourneyEntity::getExtended)
                .map(JourneyExtendedEntity::getImagesDetails)
                .map(JourneyImagesDetailsEntity::getImages)
                .filter(CollectionUtils::isNotEmpty)
                .orElse(List.of())
                .stream()
                .limit(maxImagesPerJourney)
                .toList();
    }
}
