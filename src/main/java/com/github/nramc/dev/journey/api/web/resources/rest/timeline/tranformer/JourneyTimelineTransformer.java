package com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer;

import com.github.nramc.dev.journey.api.core.journey.Journey;
import com.github.nramc.dev.journey.api.core.journey.JourneyImageDetail;
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

    public TimelineData transform(List<Journey> journeys) {
        return TimelineData.builder()
                .heading(header(journeys))
                .images(images(journeys))
                .build();
    }

    private static String header(List<Journey> journeys) {
        if (CollectionUtils.isNotEmpty(journeys) && CollectionUtils.size(journeys) == 1) {
            return journeys.getFirst().name();
        } else {
            return "Journeys";
        }
    }

    private static List<TimelineImage> images(List<Journey> journeys) {
        if (CollectionUtils.size(journeys) == 1) {
            return CollectionUtils.emptyIfNull(journeys).stream()
                    .map(JourneyTimelineTransformer::getAllImagesForJourney)
                    .flatMap(List::stream)
                    .toList();
        }

        return CollectionUtils.emptyIfNull(journeys).stream()
                .map(JourneyTimelineTransformer::getImagesForJourney)
                .flatMap(List::stream)
                .toList();
    }

    private static List<TimelineImage> getAllImagesForJourney(Journey journey) {
        return getFirstNImages(journey, Integer.MAX_VALUE).stream()
                .map(imageDetail -> toTimelineImage(imageDetail, journey))
                .toList();
    }

    private static List<TimelineImage> getImagesForJourney(Journey journey) {
        return getImages(journey).stream()
                .map(imageDetail -> toTimelineImage(imageDetail, journey))
                .toList();
    }

    private static TimelineImage toTimelineImage(JourneyImageDetail imageDetail, Journey journey) {
        return TimelineImage.builder()
                .src(imageDetail.url())
                .caption(StringUtils.firstNonBlank(imageDetail.title(), journey.name()))
                .args(Map.of())
                .build();
    }
}
