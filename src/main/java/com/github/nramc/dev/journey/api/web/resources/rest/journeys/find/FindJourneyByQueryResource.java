package com.github.nramc.dev.journey.api.web.resources.rest.journeys.find;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.security.Visibility;
import com.github.nramc.dev.journey.api.security.utils.AuthUtils;
import com.github.nramc.dev.journey.api.web.dto.Journey;
import com.github.nramc.dev.journey.api.web.dto.converter.JourneyConverter;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEYS;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Find Journey by Search Query Resource")
public class FindJourneyByQueryResource {
    private final JourneyRepository journeyRepository;

    @Operation(summary = "Find Journeys for given query")
    @RestDocCommonResponse
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns available Journeys for given query")})
    @GetMapping(value = FIND_JOURNEYS, produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Journey> find(
            @RequestParam(name = "pageIndex", defaultValue = "0") int pageIndex,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort", defaultValue = "createdDate") String sortColumn,
            @RequestParam(name = "order", defaultValue = "DESC") Sort.Direction sortOrder,
            @RequestParam(name = "publishedOnly", defaultValue = "false") boolean publishedOnly,
            @RequestParam(name = "q", defaultValue = "") String searchText,
            @RequestParam(name = "tags", defaultValue = "") List<String> tags,
            @RequestParam(name = "city", defaultValue = "") String cityText,
            @RequestParam(name = "country", defaultValue = "") String countryText,
            @RequestParam(name = "category", defaultValue = "") String categoryText,
            @RequestParam(name = "year", required = false) Long year,
            Authentication authentication) {

        Set<Visibility> visibilities = AuthUtils.getVisibilityFromAuthority(authentication.getAuthorities());
        String username = authentication.getName();
        Set<Boolean> publishedFlags = publishedOnly ? Set.of(true) : Set.of(true, false);

        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(sortOrder, sortColumn));

        List<String> tagsInLowerCase = tags.stream().map(StringUtils::lowerCase).toList();
        LocalDate journeyStartDate = year != null ? LocalDate.of(year.intValue(), 1, 1) : null;
        LocalDate journeyLastDate = year != null ? journeyStartDate.with(lastDayOfYear()) : null;

        Page<JourneyEntity> entityPage = journeyRepository.findAllBy(
                visibilities, username, publishedFlags, searchText, tagsInLowerCase,
                cityText, countryText, categoryText,
                journeyStartDate,
                journeyLastDate,
                pageable);
        Page<Journey> responsePage = entityPage.map(JourneyConverter::convert);

        log.info("Journey exists:[{}] pages:[{}] total:[{}]",
                responsePage.hasContent(), responsePage.getTotalPages(), responsePage.getTotalElements());
        return responsePage;
    }
}
