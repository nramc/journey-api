package com.github.nramc.dev.journey.api.web.resources.rest.journeys.fetch;

import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.repository.journey.projection.CategoryOnly;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Limit;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FETCH_ALL_CATEGORIES;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Categories Resource")
public class FetchAllCategoriesResource {
    private final JourneyRepository journeyRepository;

    @Operation(summary = "Get available categories from Journeys")
    @RestDocCommonResponse
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Available categories")})
    @GetMapping(value = FETCH_ALL_CATEGORIES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> find(
            @RequestParam(value = "text", defaultValue = "") String text,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        List<CategoryOnly> categories = journeyRepository.findDistinctByCategoryContainingIgnoreCaseOrderByCategory(text, Limit.of(limit));
        List<String> result = CollectionUtils.emptyIfNull(categories).stream().map(CategoryOnly::getCategory)
                .distinct().sorted().limit(limit).toList();

        log.info("Available Categories [{}]", result.size());
        return ResponseEntity.ok(result);
    }
}
