package com.github.nramc.dev.journey.api.journey.event.handler;

import com.github.nramc.dev.journey.api.journey.domain.Journey;
import com.github.nramc.dev.journey.api.journey.domain.JourneyImageDetail;
import com.github.nramc.dev.journey.api.journey.repository.JourneyService;
import com.github.nramc.dev.journey.api.shared.event.JourneyAnniversaryEvent;
import com.github.nramc.dev.journey.api.shared.provider.ActiveUserProvider;
import com.github.nramc.dev.journey.api.shared.provider.ActiveUserProvider.ActiveUser;
import com.github.nramc.dev.journey.api.shared.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.moments.DayHasPassed;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class DayHasPassedEventHandler {
    private static final int MAX_IMAGES = 10;

    private final JourneyService journeyService;
    private final ActiveUserProvider activeUserProvider;
    private final ApplicationEventPublisher applicationEvents;

    @EventListener
    @Transactional
    public void onDayHasPassed(DayHasPassed event) {
        var date = event.getDate().plusDays(1);
        List<ActiveUser> activeUsers = activeUserProvider.getActiveUsers();

        if (CollectionUtils.isEmpty(activeUsers)) {
            log.debug("Skipping anniversary processing for {} as no active users were found", date);
            return;
        }

        activeUsers.forEach(activeUser -> {
            List<Journey> journeys = journeyService.getAnniversariesInNextDays(AuthUtils.toAppUser(activeUser), 0);
            if (CollectionUtils.isEmpty(journeys)) {
                return;
            }
            applicationEvents.publishEvent(new JourneyAnniversaryEvent(
                    activeUser.emailAddress().value(),
                    activeUser.displayName(),
                    date,
                    journeys.stream().map(this::toAnniversaryItem).toList()
            ));
        });
    }

    private JourneyAnniversaryEvent.JourneyAnniversaryItem toAnniversaryItem(Journey journey) {
        List<String> imageUrls = journey.imagesDetails() == null
                ? List.of()
                : journey.imagesDetails().images().stream().map(JourneyImageDetail::url)
                .filter(url -> !journey.thumbnail().equalsIgnoreCase(url))
                .limit(MAX_IMAGES)
                .toList();

        return new JourneyAnniversaryEvent.JourneyAnniversaryItem(
                journey.id(),
                journey.name(),
                journey.journeyDate(),
                journey.geoDetails().title(),
                journey.thumbnail(),
                imageUrls
        );
    }

}
