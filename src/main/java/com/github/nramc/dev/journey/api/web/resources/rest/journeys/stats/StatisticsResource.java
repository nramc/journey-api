package com.github.nramc.dev.journey.api.web.resources.rest.journeys.stats;

import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyGeoDetailsEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyService;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.utils.AuthUtils;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.stats.StatisticsResponse.KeyValueStatistics;
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
import java.util.Set;
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
        Set<Visibility> visibilities = AuthUtils.getVisibilityFromAuthority(authentication.getAuthorities());
        AppUser user = AppUser.builder().username(authentication.getName()).build();

        List<JourneyEntity> entities = journeyService.findAllPublishedJourneys(user, visibilities);

        return StatisticsResponse.builder()
                .categories(getStatsFor(entities, journeyEntity -> Optional.of(journeyEntity).map(JourneyEntity::getExtended)
                        .map(JourneyExtendedEntity::getGeoDetails).map(JourneyGeoDetailsEntity::getCategory).orElse(StringUtils.EMPTY)
                ))
                .cities(getStatsFor(entities, journeyEntity -> Optional.of(journeyEntity).map(JourneyEntity::getExtended)
                        .map(JourneyExtendedEntity::getGeoDetails).map(JourneyGeoDetailsEntity::getCity).orElse(StringUtils.EMPTY)
                ))
                .countries(getStatsFor(entities, journeyEntity -> Optional.of(journeyEntity).map(JourneyEntity::getExtended)
                        .map(JourneyExtendedEntity::getGeoDetails).map(JourneyGeoDetailsEntity::getCountry).orElse(StringUtils.EMPTY)
                ))
                .years(getStatsFor(entities, entity -> String.valueOf(entity.getJourneyDate().getYear())))
                .build();
    }

    private static List<KeyValueStatistics> getStatsFor(List<JourneyEntity> entities, Function<JourneyEntity, String> fnExtractField) {
        Map<String, Long> countByCategory = CollectionUtils.emptyIfNull(entities).stream()
                .collect(Collectors.groupingBy(fnExtractField, Collectors.counting()));

        return countByCategory.entrySet().stream()
                .map(entry -> new KeyValueStatistics(entry.getKey(), entry.getValue()))
                .toList();
    }
}
