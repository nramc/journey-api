package com.github.nramc.dev.journey.api.notification.processor;

import com.github.nramc.dev.journey.api.infrastructure.actuator.ApplicationProperties;
import com.github.nramc.dev.journey.api.shared.event.JourneyAnniversaryEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;

class JourneyAnniversaryNotificationProcessorTest {
    private static final ApplicationProperties PROPERTIES = ApplicationProperties.builder()
            .name("journey")
            .version("1.0.0")
            .uiAppUrl("https://journey.codewithram.dev/")
            .build();

    private final JourneyAnniversaryNotificationProcessor processor = new JourneyAnniversaryNotificationProcessor(PROPERTIES);


    @Test
    void process_shouldBuildTemplateNotificationWithJourneyLinks() {
        var event = new JourneyAnniversaryEvent(
                "john@example.com",
                "John",
                LocalDate.of(2026, Month.JUNE, 12),
                List.of(new JourneyAnniversaryEvent.JourneyAnniversaryItem(
                        "abc123",
                        "Trip to Rome",
                        LocalDate.of(2024, Month.JUNE, 12),
                        "Rome, Italy",
                        null,
                        List.of("https://img.example/1.jpg")
                ))
        );

        var result = processor.process(event);

        assertThat(result).isPresent();
        var notificationData = result.orElseThrow();
        assertThat(notificationData.subject()).isEqualTo("On This Day: Your Journey Memories");
        assertThat(notificationData.recipients()).containsExactly("john@example.com");

        assertThat(notificationData.metadata()).containsEntry("template", "journey-anniversary-postcard-template.html");
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) notificationData.metadata().get("metadata");
        assertThat(metadata)
                .containsEntry("recipientName", "John")
                .containsEntry("anniversaryDate", "12 Jun 2026")
                .containsEntry("journeyCount", 1);

        @SuppressWarnings("unchecked")
        var journeys = (List<Map<String, Object>>) metadata.get("journeys");
        assertThat(journeys).hasSize(1).first()
                .asInstanceOf(MAP)
                .containsEntry("journeyTitle", "Trip to Rome")
                .containsEntry("journeyDate", "12 Jun 2024")
                .containsEntry("geoLocation", "Rome, Italy")
                .containsEntry("thumbnailUrl", "")
                .containsEntry("journeyUrl", "https://journey.codewithram.dev/journey/abc123/view");
    }

    @Test
    void process_shouldReturnEmptyWhenJourneyListIsEmpty() {
        var event = new JourneyAnniversaryEvent(
                "john@example.com",
                "John",
                LocalDate.of(2026, Month.JUNE, 12),
                List.of()
        );

        var result = processor.process(event);

        assertThat(result).isEmpty();
    }
}
