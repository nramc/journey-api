package com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.TimelineData;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.TimelineData.TimelineImage;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer.TimelineDataTransformer.DEFAULT_HEADING;
import static com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer.TimelineDataTransformer.getImages;

@UtilityClass
public class YearTimelineTransformer {

    public TimelineData transform(List<JourneyEntity> entities, List<Long> years) {
        return TimelineData.builder()
                .heading(header(years))
                .images(images(entities))
                .build();
    }

    private static String header(List<Long> years) {
        if (CollectionUtils.isNotEmpty(years)) {
            return CollectionUtils.size(years) == 1 ? String.valueOf(years.getFirst()) : years.getFirst() + " - " + years.getLast();
        } else {
            return DEFAULT_HEADING;
        }
    }

    private static List<TimelineImage> images(List<JourneyEntity> entities) {
        return CollectionUtils.emptyIfNull(entities).stream()
                .map(YearTimelineTransformer::getImagesForJourney)
                .flatMap(List::stream)
                .toList();
    }

    private static List<TimelineImage> getImagesForJourney(JourneyEntity journeyEntity) {
        return getImages(journeyEntity).stream()
                .map(imageEntity -> toTimelineImage(imageEntity, journeyEntity))
                .toList();
    }

    private static TimelineImage toTimelineImage(JourneyImageDetailEntity imageDetail, JourneyEntity journey) {
        String title = Optional.ofNullable(journey.getJourneyDate()).map(LocalDate::getYear).map(String::valueOf).orElse(null);
        return TimelineImage.builder()
                .title(title)
                .src(imageDetail.getUrl())
                .caption(imageDetail.getTitle())
                .args(Map.of())
                .build();
    }
}
