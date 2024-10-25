package com.github.nramc.dev.journey.api.web.resources.rest.timeline;

import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.journey.Journey;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import com.github.nramc.dev.journey.api.repository.journey.JourneySearchCriteria;
import com.github.nramc.dev.journey.api.repository.journey.JourneyService;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.utils.AuthUtils;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer.TimelineDataTransformer;
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
import java.util.Set;

import static com.github.nramc.dev.journey.api.web.resources.Resources.GET_TIMELINE_DATA;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Timeline")
public class TimelineResource {
    private final JourneyService journeyService;


    @Operation(summary = "Get Timeline data")
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
                .journeyDaysUpTo(BooleanUtils.isTrue(today) ? 0 : upcomingJourneysTillDays)
                .build();

        List<Journey> journeys = journeyService.findAllJourneys(searchCriteria);
        return TimelineDataTransformer.transform(journeys, journeyIDs, cities, countries, categories, years, today,
                upcomingJourneysTillDays != null);
    }


}
