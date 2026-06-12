package com.github.nramc.dev.journey.api.journey.event.handler;

import com.github.nramc.dev.journey.api.infrastructure.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.journey.domain.Journey;
import com.github.nramc.dev.journey.api.journey.repository.JourneyService;
import com.github.nramc.dev.journey.api.journey.repository.converter.JourneyConverter;
import com.github.nramc.dev.journey.api.journey.web.journeys.JourneyData;
import com.github.nramc.dev.journey.api.shared.domain.AppUser;
import com.github.nramc.dev.journey.api.shared.domain.EmailAddress;
import com.github.nramc.dev.journey.api.shared.domain.user.security.Role;
import com.github.nramc.dev.journey.api.shared.event.JourneyAnniversaryDetectedEvent;
import com.github.nramc.dev.journey.api.shared.event.JourneyAnniversaryDetectedEvent.JourneyAnniversaryItem;
import com.github.nramc.dev.journey.api.shared.provider.ActiveUserProvider;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.moments.DayHasPassed;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ApplicationModuleTest(extraIncludes = "infrastructure")
@ActiveProfiles("test")
@Import(TestContainersConfiguration.class)
class DayHasPassedEventHandlerTest {
    @MockitoBean
    private JourneyService journeyService;
    @MockitoBean
    private ActiveUserProvider activeUserProvider;

    @Test
    void onDayHasPassed_whenAnniversariesFound_shouldPublishMappedJourneyEvent(Scenario scenario) {
        LocalDate date = LocalDate.of(2026, 6, 12);
        var activeUser = new ActiveUserProvider.ActiveUser(
                EmailAddress.valueOf("john@example.com"),
                "John",
                Set.of(Role.AUTHENTICATED_USER)
        );
        Journey anniversaryJourney = JourneyConverter.convert(JourneyData.JOURNEY_ENTITY.toBuilder()
                .id("J-100")
                .name("Munich Journey")
                .journeyDate(LocalDate.of(2020, 6, 12))
                .build());

        when(activeUserProvider.getActiveUsers()).thenReturn(List.of(activeUser));
        when(journeyService.getAnniversariesInNextDays(any(AppUser.class), eq(0))).thenReturn(List.of(anniversaryJourney));

        scenario.publish(DayHasPassed.of(date))
                .andWaitAtMost(Duration.ofSeconds(5))
                .forEventOfType(JourneyAnniversaryDetectedEvent.class)
                .matching(event -> event.username().equals(activeUser.emailAddress().value()))
                .toArriveAndVerify(detected -> {
                    assertThat(detected).extracting(JourneyAnniversaryDetectedEvent::username, JourneyAnniversaryDetectedEvent::recipientName,
                                    JourneyAnniversaryDetectedEvent::date)
                            .containsExactly(activeUser.emailAddress().value(), activeUser.displayName(), date);

                    assertThat(detected.journeys()).hasSize(1).first().isNotNull()
                            .satisfies(journey -> assertThat(journey)
                                    .extracting(JourneyAnniversaryItem::journeyId, JourneyAnniversaryItem::journeyTitle, JourneyAnniversaryItem::geoLocation,
                                            JourneyAnniversaryItem::thumbnailUrl)
                                    .containsExactly("J-100", "Munich Journey", "Airport, Munich, Germany", "https://example.com/thumbnail.png"))
                            .satisfies(journey -> assertThat(journey.imageUrls()).hasSize(2).containsExactly("image1.jpg", "image2.jpg"));
                });

        verify(journeyService).getAnniversariesInNextDays(argThat(matchesAppUser(activeUser)), eq(0));
    }

    private org.mockito.ArgumentMatcher<AppUser> matchesAppUser(ActiveUserProvider.ActiveUser activeUser) {
        return appUser -> appUser != null
                && activeUser.emailAddress().value().equals(appUser.username())
                && activeUser.displayName().equals(appUser.name())
                && activeUser.roles().equals(appUser.roles())
                && appUser.enabled();
    }

}





