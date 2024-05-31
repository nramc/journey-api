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

@UtilityClass
public class TimelineDataTransformer {
    public static final String DEFAULT_HEADING = "Timeline";

    public static TimelineData transform(List<JourneyEntity> entities,
                                         List<String> journeyIDs,
                                         List<String> cities,
                                         List<String> countries,
                                         List<Long> years,
                                         Boolean today,
                                         Boolean upcoming) {
        if (CollectionUtils.isNotEmpty(years)) {
            return TimelineData.builder()
                    .heading(header(years))
                    .images(getImagesForTimeline(entities))
                    .build();
        } else {
            return TimelineData.builder()
                    .heading(DEFAULT_HEADING)
                    .title("timeline-title")
                    .images(getImagesForTimeline(entities))
                    .build();
        }
    }

    private static String header(List<Long> years) {
        if (CollectionUtils.isNotEmpty(years)) {
            return CollectionUtils.size(years) == 1 ? String.valueOf(years.getFirst()) : years.getFirst() + " - " + years.getLast();
        } else {
            return DEFAULT_HEADING;
        }
    }

    private static List<TimelineData.TimelineImage> getImagesForTimeline(List<JourneyEntity> entities) {
        return CollectionUtils.emptyIfNull(entities).stream()
                .map(JourneyEntity::getExtended)
                .map(JourneyExtendedEntity::getImagesDetails)
                .map(JourneyImagesDetailsEntity::getImages)
                .flatMap(Collection::stream)
                .filter(JourneyImageDetailEntity::isFavorite)
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
}
