package com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer;

import com.github.nramc.dev.journey.api.core.journey.Journey;
import com.github.nramc.dev.journey.api.core.journey.JourneyImageDetail;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.TimelineData;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.TimelineData.TimelineImage;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer.TimelineDataTransformer.getImages;

@UtilityClass
public class UpcomingTimelineTransformer {

    public TimelineData transform(List<Journey> journeys) {
        return TimelineData.builder()
                .heading("Upcoming Journiversaries")
                .images(images(journeys))
                .build();
    }

    private static List<TimelineImage> images(List<Journey> journeys) {
        return CollectionUtils.emptyIfNull(journeys).stream()
                .map(UpcomingTimelineTransformer::getImagesForJourney)
                .flatMap(List::stream)
                .toList();
    }

    private static List<TimelineImage> getImagesForJourney(Journey journey) {
        return getImages(journey).stream()
                .map(imageDetail -> toTimelineImage(imageDetail, journey))
                .toList();
    }

    private static TimelineImage toTimelineImage(JourneyImageDetail imageDetail, Journey journey) {
        String title = Optional.ofNullable(journey.journeyDate()).map(UpcomingTimelineTransformer::title).orElse(null);
        return TimelineImage.builder()
                .title(title)
                .src(imageDetail.url())
                .caption(StringUtils.firstNonBlank(imageDetail.title(), journey.name()))
                .args(Map.of())
                .build();
    }

    private static String title(LocalDate journeyDate) {
        return journeyDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
