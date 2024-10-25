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

import static com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer.TimelineDataTransformer.getImages;

@UtilityClass
public class CountryTimelineTransformer {

    public TimelineData transform(List<Journey> journeys) {
        return TimelineData.builder()
                .heading("Country")
                .images(images(journeys))
                .build();
    }

    private static List<TimelineImage> images(List<Journey> journeys) {
        return CollectionUtils.emptyIfNull(journeys).stream()
                .map(CountryTimelineTransformer::getImagesForJourney)
                .flatMap(List::stream)
                .toList();
    }

    private static List<TimelineImage> getImagesForJourney(Journey journey) {
        return getImages(journey).stream()
                .map(image -> toTimelineImage(image, journey))
                .toList();
    }

    private static TimelineImage toTimelineImage(JourneyImageDetail imageDetail, Journey journey) {
        return TimelineImage.builder()
                .title(journey.extendedDetails().geoDetails().country())
                .src(imageDetail.url())
                .caption(StringUtils.firstNonBlank(imageDetail.title(), journey.name()))
                .args(Map.of())
                .build();
    }
}
