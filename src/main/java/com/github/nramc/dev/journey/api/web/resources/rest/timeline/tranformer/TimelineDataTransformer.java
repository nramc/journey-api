package com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer;

import com.github.nramc.dev.journey.api.core.journey.Journey;
import com.github.nramc.dev.journey.api.core.journey.JourneyExtendedDetails;
import com.github.nramc.dev.journey.api.core.journey.JourneyImageDetail;
import com.github.nramc.dev.journey.api.core.journey.JourneyImagesDetails;
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
    public static TimelineData transform(List<Journey> journeys,
                                         List<String> journeyIDs,
                                         List<String> cities,
                                         List<String> countries,
                                         List<String> categories,
                                         List<Long> years,
                                         Boolean today,
                                         Boolean upcoming) {
        if (Boolean.TRUE.equals(today)) {
            return TodayTimelineTransformer.transform(journeys);
        } else if (Boolean.TRUE.equals(upcoming)) {
            return UpcomingTimelineTransformer.transform(journeys);
        } else if (CollectionUtils.isNotEmpty(years)) {
            return YearTimelineTransformer.transform(journeys, years);
        } else if (CollectionUtils.isNotEmpty(journeyIDs)) {
            return JourneyTimelineTransformer.transform(journeys);
        } else if (CollectionUtils.isNotEmpty(cities)) {
            return CityTimelineTransformer.transform(journeys);
        } else if (CollectionUtils.isNotEmpty(countries)) {
            return CountryTimelineTransformer.transform(journeys);
        } else if (CollectionUtils.isNotEmpty(categories)) {
            return CategoryTimelineTransformer.transform(journeys);
        } else {
            return TimelineData.builder()
                    .heading(DEFAULT_HEADING)
                    .images(getImagesForTimeline(journeys))
                    .build();
        }
    }

    private static List<TimelineData.TimelineImage> getImagesForTimeline(List<Journey> journeys) {
        return CollectionUtils.emptyIfNull(journeys).stream()
                .map(TimelineDataTransformer::getImages)
                .flatMap(Collection::stream)
                .map(TimelineDataTransformer::toTimelineImage)
                .toList();
    }

    private static TimelineData.TimelineImage toTimelineImage(JourneyImageDetail imageDetail) {
        return TimelineData.TimelineImage.builder()
                .src(imageDetail.url())
                .caption(imageDetail.title())
                .args(Map.of())
                .build();
    }

    public static List<JourneyImageDetail> getImages(Journey journey) {
        List<JourneyImageDetail> favoriteImages = getFavoriteImages(journey);
        return favoriteImages.isEmpty() ? getFirstNImages(journey, MAX_IMAGES_PER_JOURNEY) : favoriteImages;
    }

    private static List<JourneyImageDetail> getFavoriteImages(Journey journey) {
        return Optional.of(journey)
                .map(Journey::extendedDetails)
                .map(JourneyExtendedDetails::imagesDetails)
                .map(JourneyImagesDetails::images)
                .filter(CollectionUtils::isNotEmpty)
                .orElse(List.of())
                .stream()
                .filter(JourneyImageDetail::isFavorite)
                .limit(MAX_IMAGES_PER_JOURNEY)
                .toList();
    }

    public static List<JourneyImageDetail> getFirstNImages(Journey journey, int maxImagesPerJourney) {
        return Optional.of(journey)
                .map(Journey::extendedDetails)
                .map(JourneyExtendedDetails::imagesDetails)
                .map(JourneyImagesDetails::images)
                .filter(CollectionUtils::isNotEmpty)
                .orElse(List.of())
                .stream()
                .limit(maxImagesPerJourney)
                .toList();
    }
}
