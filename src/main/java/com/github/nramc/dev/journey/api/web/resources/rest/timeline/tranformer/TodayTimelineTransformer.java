package com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.TimelineData;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.TimelineData.TimelineImage;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer.TimelineDataTransformer.getImages;

@UtilityClass
public class TodayTimelineTransformer {

    public TimelineData transform(List<JourneyEntity> entities) {
        return TimelineData.builder()
                .heading("Today in History")
                .images(images(entities))
                .build();
    }

    private static List<TimelineImage> images(List<JourneyEntity> entities) {
        return CollectionUtils.emptyIfNull(entities).stream()
                .map(TodayTimelineTransformer::getImagesForJourney)
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
                .caption(StringUtils.firstNonBlank(imageDetail.getTitle(), journey.getName()))
                .args(Map.of())
                .build();
    }
}
