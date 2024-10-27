package com.github.nramc.dev.journey.api.web.resources.rest.statistics;

import com.github.nramc.dev.journey.api.core.journey.Journey;
import com.github.nramc.dev.journey.api.core.journey.JourneyGeoDetails;
import com.github.nramc.dev.journey.api.repository.journey.JourneyService;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.utils.AuthUtils;
import com.github.nramc.dev.journey.api.web.resources.rest.statistics.StatisticsResponse.KeyValueStatistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.nramc.dev.journey.api.web.resources.Resources.GET_STATISTICS;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Statistics")
public class StatisticsResource {
    private final JourneyService journeyService;

    @Operation(summary = "Get Statistics about all available Journeys")
    @GetMapping(value = GET_STATISTICS, produces = APPLICATION_JSON_VALUE)
    public StatisticsResponse getStatistics(Authentication authentication) {

        List<Journey> journeys = journeyService.findAllPublishedJourneys(AuthUtils.toAppUser(authentication));

        return StatisticsResponse.builder()
                .categories(getStatsFor(journeys, journey -> Optional.of(journey).map(Journey::geoDetails)
                        .map(JourneyGeoDetails::category).orElse(StringUtils.EMPTY)
                ))
                .cities(getStatsFor(journeys, journey -> Optional.of(journey).map(Journey::geoDetails)
                        .map(JourneyGeoDetails::city).orElse(StringUtils.EMPTY)
                ))
                .countries(getStatsFor(journeys, journey -> Optional.of(journey).map(Journey::geoDetails)
                        .map(JourneyGeoDetails::country).orElse(StringUtils.EMPTY)
                ))
                .years(getStatsFor(journeys, journey -> String.valueOf(journey.journeyDate().getYear())))
                .build();
    }

    private static List<KeyValueStatistics> getStatsFor(List<Journey> entities, Function<Journey, String> fnExtractField) {
        Map<String, Long> countByCategory = CollectionUtils.emptyIfNull(entities).stream()
                .collect(Collectors.groupingBy(fnExtractField, Collectors.counting()));

        return countByCategory.entrySet().stream()
                .map(entry -> new KeyValueStatistics(entry.getKey(), entry.getValue()))
                .toList();
    }
}
