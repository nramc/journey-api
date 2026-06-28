package com.github.nramc.dev.journey.api.notification.processor;

import com.github.nramc.dev.journey.api.infrastructure.actuator.ApplicationProperties;
import com.github.nramc.dev.journey.api.notification.NotificationData;
import com.github.nramc.dev.journey.api.notification.NotificationEventProcessor;
import com.github.nramc.dev.journey.api.shared.event.JourneyAnniversaryEvent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.NonNull;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class JourneyAnniversaryNotificationProcessor
        implements NotificationEventProcessor<JourneyAnniversaryEvent> {

    private static final String ANNIVERSARY_EMAIL_TEMPLATE = "journey-anniversary-postcard-template.html";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM uuuu");

    private final ApplicationProperties applicationProperties;

    @Override
    public @NonNull Class<JourneyAnniversaryEvent> type() {
        return JourneyAnniversaryEvent.class;
    }

    @Override
    public @NonNull Optional<NotificationData> process(@NonNull JourneyAnniversaryEvent event) {
        if (event.journeys() == null || event.journeys().isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("recipientName", event.recipientName());
        placeholders.put("anniversaryDate", DATE_FORMATTER.format(event.date()));
        placeholders.put("journeyCount", event.journeys().size());
        placeholders.put("journeys", event.journeys().stream().map(journey -> Map.of(
                "journeyTitle", journey.journeyTitle(),
                "journeyDate", DATE_FORMATTER.format(journey.journeyDate()),
                "geoLocation", journey.geoLocation(),
                "thumbnailUrl", StringUtils.defaultString(journey.thumbnailUrl()),
                "imageUrls", journey.imageUrls(),
                "journeyUrl", buildJourneyUrl(journey.journeyId())
        )).toList());

        return Optional.of(NotificationData.ofEmail(
                "On This Day: Your Journey Memories",
                List.of(event.username()),
                ANNIVERSARY_EMAIL_TEMPLATE,
                placeholders
        ));
    }

    private String buildJourneyUrl(String journeyId) {
        String baseUrl = Strings.CS.removeEnd(applicationProperties.uiAppUrl(), "/");
        return baseUrl + "/journey/" + journeyId + "/view";
    }
}
