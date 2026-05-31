package com.github.nramc.dev.journey.api.journey.web.timeline;

import com.github.nramc.dev.journey.api.journey.domain.Journey;
import com.github.nramc.dev.journey.api.journey.repository.JourneySearchCriteria;
import com.github.nramc.dev.journey.api.journey.repository.JourneyService;
import com.github.nramc.dev.journey.api.shared.domain.AppUser;
import com.github.nramc.dev.journey.api.shared.domain.user.security.Visibility;
import com.github.nramc.dev.journey.api.shared.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.github.nramc.dev.journey.api.shared.web.Resources.GET_TIMELINE_DATA;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Timeline")
public class TimelineResource {
    private final JourneyService journeyService;

    @Operation(summary = "Get Journeys for timeline")
    @GetMapping(value = GET_TIMELINE_DATA, produces = APPLICATION_JSON_VALUE)
    public TimelineData getTimelineData(
            @RequestParam(name = "IDs", defaultValue = "") List<String> journeyIDs,
            @RequestParam(name = "city", defaultValue = "") List<String> cities,
            @RequestParam(name = "country", defaultValue = "") List<String> countries,
            @RequestParam(name = "category", defaultValue = "") List<String> categories,
            @RequestParam(name = "year", required = false) List<Long> years,
            @RequestParam(name = "today", required = false) Boolean today,
            @RequestParam(name = "upcoming", required = false) Integer upcomingJourneysTillDays,
            Authentication authentication) {

        Set<Visibility> visibilities = AuthUtils.getVisibilityFromAuthority(authentication.getAuthorities());
        String username = authentication.getName();

        JourneySearchCriteria searchCriteria = JourneySearchCriteria.builder()
                .publishedFlags(Set.of(true))
                .visibilities(visibilities)
                .appUser(AppUser.builder().username(username).build())
                .ids(journeyIDs)
                .cities(cities)
                .countries(countries)
                .categories(categories)
                .journeyYears(years)
                .journeyDaysUpTo(Optional.ofNullable(today).filter(BooleanUtils::isTrue).map(ignore -> 0).orElse(upcomingJourneysTillDays))
                .build();

        List<Journey> journeys = journeyService.findAllJourneys(searchCriteria);
        return TimelineData.builder()
                .heading(TimelineHeadingResolver.resolve(
                        journeyIDs, cities, countries, categories, years, today, upcomingJourneysTillDays, journeys))
                .journeys(journeys)
                .build();
    }
}
