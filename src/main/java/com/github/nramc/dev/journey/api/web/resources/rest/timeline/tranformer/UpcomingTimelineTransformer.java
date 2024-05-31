package com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImagesDetailsEntity;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.TimelineData;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.TimelineData.TimelineImage;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@UtilityClass
public class UpcomingTimelineTransformer {
    private static final int MAX_IMAGES_PER_JOURNEY = 3;

    public TimelineData transform(List<JourneyEntity> entities) {
        return TimelineData.builder()
                .heading("Upcoming")
                .images(images(entities))
                .build();
    }

    private static List<TimelineImage> images(List<JourneyEntity> entities) {
        return CollectionUtils.emptyIfNull(entities).stream()
                .map(UpcomingTimelineTransformer::getImagesForJourney)
                .flatMap(List::stream)
                .toList();
    }

    private static List<TimelineImage> getImagesForJourney(JourneyEntity journeyEntity) {
        return Stream.of(journeyEntity)
                .map(JourneyEntity::getExtended)
                .map(JourneyExtendedEntity::getImagesDetails)
                .map(JourneyImagesDetailsEntity::getImages)
                .flatMap(Collection::stream)
                .filter(JourneyImageDetailEntity::isFavorite)
                .limit(MAX_IMAGES_PER_JOURNEY)
                .map(imageEntity -> toTimelineImage(imageEntity, journeyEntity))
                .toList();
    }

    private static TimelineImage toTimelineImage(JourneyImageDetailEntity imageDetail, JourneyEntity journey) {
        String title = Optional.ofNullable(journey.getJourneyDate()).map(UpcomingTimelineTransformer::title).orElse(null);
        return TimelineImage.builder()
                .title(title)
                .src(imageDetail.getUrl())
                .caption(imageDetail.getTitle())
                .args(Map.of())
                .build();
    }

    private static String title(LocalDate journeyDate) {
        return LocalDate.now()
                .withDayOfMonth(journeyDate.getDayOfMonth())
                .withMonth(journeyDate.getMonthValue())
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
