package com.github.nramc.dev.journey.api.shared.event;

import java.time.LocalDate;
import java.util.List;

/**
 * Published when anniversary journeys are detected for one user on a given date.
 *
 * @param username recipient e-mail address
 * @param recipientName display name used in personalized messages
 * @param date date that triggered the anniversary detection
 * @param journeys all journeys owned by the user matching month/day on {@code date}
 */
public record JourneyAnniversaryEvent(
        String username,
        String recipientName,
        LocalDate date,
        List<JourneyAnniversaryItem> journeys
) {

    public record JourneyAnniversaryItem(
            String journeyId,
            String journeyTitle,
            LocalDate journeyDate,
            String geoLocation,
            String thumbnailUrl,
            List<String> imageUrls
    ) {
    }
}
