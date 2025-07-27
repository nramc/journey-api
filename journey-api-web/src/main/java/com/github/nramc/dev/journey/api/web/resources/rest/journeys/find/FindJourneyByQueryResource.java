package com.github.nramc.dev.journey.api.web.resources.rest.journeys.find;

import com.github.nramc.dev.journey.api.core.domain.AppUser;
import com.github.nramc.dev.journey.api.core.domain.data.DataPageable;
import com.github.nramc.dev.journey.api.core.journey.Journey;
import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import com.github.nramc.dev.journey.api.repository.journey.JourneySearchCriteria;
import com.github.nramc.dev.journey.api.repository.journey.JourneyService;
import com.github.nramc.dev.journey.api.repository.journey.PagingProperty;
import com.github.nramc.dev.journey.api.web.resources.rest.auth.utils.AuthUtils;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEYS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_UPCOMING_ANNIVERSARY;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FindJourneyByQueryResource {
    private final JourneyService journeyService;

    @Operation(summary = "Find Journeys for given query", tags = {"Search Journey"})
    @RestDocCommonResponse
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns available Journeys for given query")})
    @GetMapping(value = FIND_JOURNEYS, produces = MediaType.APPLICATION_JSON_VALUE)
    public DataPageable<Journey> find(
            @RequestParam(name = "pageIndex", defaultValue = "0") int pageIndex,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort", defaultValue = "createdDate") String sortColumn,
            @RequestParam(name = "order", defaultValue = "DESC") String sortDirection,
            @RequestParam(name = "publishedOnly", defaultValue = "false") boolean publishedOnly,
            @RequestParam(name = "q", defaultValue = "") String searchText,
            @RequestParam(name = "tags", defaultValue = "") List<String> tags,
            @RequestParam(name = "city", defaultValue = "") List<String> cities,
            @RequestParam(name = "country", defaultValue = "") List<String> countries,
            @RequestParam(name = "category", defaultValue = "") List<String> categories,
            @RequestParam(name = "year", required = false) Long year,
            Authentication authentication) {

        Set<Visibility> visibilities = AuthUtils.getVisibilityFromAuthority(authentication.getAuthorities());
        String username = authentication.getName();
        Set<Boolean> publishedFlags = publishedOnly ? Set.of(true) : Set.of(true, false);

        List<String> tagsInLowerCase = tags.stream().map(StringUtils::lowerCase).toList();
        LocalDate journeyStartDate = year != null ? LocalDate.of(year.intValue(), 1, 1) : null;
        LocalDate journeyLastDate = year != null ? journeyStartDate.with(lastDayOfYear()) : null;

        JourneySearchCriteria searchCriteria = JourneySearchCriteria.builder()
                .appUser(AppUser.builder().username(username).build())
                .visibilities(visibilities)
                .publishedFlags(publishedFlags)
                .searchText(searchText)
                .tags(tagsInLowerCase)
                .cities(cities)
                .countries(countries)
                .categories(categories)
                .journeyDateFrom(journeyStartDate)
                .journeyDateUpTo(journeyLastDate)
                .build();
        PagingProperty pagingProperty = PagingProperty.builder()
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .sortColumn(sortColumn)
                .sortDirection(sortDirection)
                .build();

        DataPageable<Journey> journeysWithPagination = journeyService.findAllJourneysWithPagination(searchCriteria, pagingProperty);

        log.info("Journey exists:[{}] pages:[{}] total:[{}]",
                CollectionUtils.size(journeysWithPagination.content()),
                journeysWithPagination.totalPages(),
                journeysWithPagination.totalElements()
        );
        return journeysWithPagination;
    }

    @GetMapping(path = FIND_UPCOMING_ANNIVERSARY, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Journey> getUpcomingAnniversaries(
            @RequestParam(value = "days", defaultValue = "1") int upcomingAnniversaryDays,
            Authentication authentication) {
        AppUser user = AuthUtils.toAppUser(authentication);
        return journeyService.getAnniversariesInNextDays(user, upcomingAnniversaryDays);
    }

}
