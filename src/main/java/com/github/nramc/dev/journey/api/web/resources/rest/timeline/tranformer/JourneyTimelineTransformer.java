package com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.TimelineData;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.TimelineData.TimelineImage;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer.TimelineDataTransformer.getFirstNImages;
import static com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer.TimelineDataTransformer.getImages;

@UtilityClass
public class JourneyTimelineTransformer {

    public TimelineData transform(List<JourneyEntity> entities) {
        return TimelineData.builder()
                .heading(header(entities))
                .images(images(entities))
                .build();
    }

    private static String header(List<JourneyEntity> entities) {
        if (CollectionUtils.isNotEmpty(entities) && CollectionUtils.size(entities) == 1) {
            return entities.getFirst().getName();
        } else {
            return "Journeys";
        }
    }

    private static List<TimelineImage> images(List<JourneyEntity> entities) {
        if (CollectionUtils.size(entities) == 1) {
            return CollectionUtils.emptyIfNull(entities).stream()
                    .map(JourneyTimelineTransformer::getAllImagesForJourney)
                    .flatMap(List::stream)
                    .toList();
        }

        return CollectionUtils.emptyIfNull(entities).stream()
                .map(JourneyTimelineTransformer::getImagesForJourney)
                .flatMap(List::stream)
                .toList();
    }

    private static List<TimelineImage> getAllImagesForJourney(JourneyEntity journeyEntity) {
        return getFirstNImages(journeyEntity, Integer.MAX_VALUE).stream()
                .map(imageEntity -> toTimelineImage(imageEntity, journeyEntity))
                .toList();
    }

    private static List<TimelineImage> getImagesForJourney(JourneyEntity journeyEntity) {
        return getImages(journeyEntity).stream()
                .map(imageEntity -> toTimelineImage(imageEntity, journeyEntity))
                .toList();
    }

    private static TimelineImage toTimelineImage(JourneyImageDetailEntity imageDetail, JourneyEntity journey) {
        return TimelineImage.builder()
                .title(journey.getName())
                .src(imageDetail.getUrl())
                .caption(StringUtils.firstNonBlank(imageDetail.getTitle(), journey.getName()))
                .args(Map.of())
                .build();
    }
}
