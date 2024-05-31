package com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.TimelineData;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.TimelineData.TimelineImage;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer.TimelineDataTransformer.getImages;

@UtilityClass
public class CityTimelineTransformer {

    public TimelineData transform(List<JourneyEntity> entities, List<String> cities) {
        return TimelineData.builder()
                .heading(header(cities))
                .images(images(entities))
                .build();
    }

    private static String header(List<String> cities) {
        if (CollectionUtils.isNotEmpty(cities) && CollectionUtils.size(cities) == 1) {
            return cities.getFirst();
        } else {
            return "Cities";
        }
    }

    private static List<TimelineImage> images(List<JourneyEntity> entities) {
        return CollectionUtils.emptyIfNull(entities).stream()
                .map(CityTimelineTransformer::getImagesForJourney)
                .flatMap(List::stream)
                .toList();
    }

    private static List<TimelineImage> getImagesForJourney(JourneyEntity journeyEntity) {
        return getImages(journeyEntity).stream()
                .map(imageEntity -> toTimelineImage(imageEntity, journeyEntity))
                .toList();
    }

    private static TimelineImage toTimelineImage(JourneyImageDetailEntity imageDetail, JourneyEntity journey) {
        return TimelineImage.builder()
                .title(journey.getCity())
                .src(imageDetail.getUrl())
                .caption(imageDetail.getTitle())
                .args(Map.of())
                .build();
    }
}