package com.github.nramc.dev.journey.api.web.resources.rest.journeys.fetch;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FETCH_ALL_CATEGORIES;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FetchAllCategoriesResource {
    private final MongoTemplate mongoTemplate;

    @GetMapping(value = FETCH_ALL_CATEGORIES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> find(@RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<String> categories = mongoTemplate.findDistinct("category", JourneyEntity.class, String.class);
        List<String> result = CollectionUtils.emptyIfNull(categories).stream().sorted().limit(limit).toList();

        log.info("Available Categories [{}]", result.size());
        return ResponseEntity.ok(result);
    }
}
