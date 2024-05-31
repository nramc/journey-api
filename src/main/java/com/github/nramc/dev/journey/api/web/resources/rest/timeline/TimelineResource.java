package com.github.nramc.dev.journey.api.web.resources.rest.timeline;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.security.Visibility;
import com.github.nramc.dev.journey.api.security.utils.AuthUtils;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.tranformer.TimelineDataTransformer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.github.nramc.dev.journey.api.web.resources.Resources.GET_TIMELINE_DATA;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Timeline Resource")
public class TimelineResource {
    static final long DAYS_FOR_UPCOMING_TIMELINE = 7;
    private final MongoTemplate mongoTemplate;

    @Operation(summary = "Get Timeline data")
    @GetMapping(value = GET_TIMELINE_DATA, produces = APPLICATION_JSON_VALUE)
    @SuppressWarnings("java:S1192")
    public TimelineData getTimelineData(
            @RequestParam(name = "IDs", defaultValue = "") List<String> journeyIDs,
            @RequestParam(name = "city", defaultValue = "") List<String> cities,
            @RequestParam(name = "country", defaultValue = "") List<String> countries,
            @RequestParam(name = "year", required = false) List<Long> years,
            @RequestParam(name = "today", required = false) Boolean today,
            @RequestParam(name = "upcoming", required = false) Boolean upcoming,
            Authentication authentication) {
        Set<Visibility> visibilities = AuthUtils.getVisibilityFromAuthority(authentication.getAuthorities());
        String username = authentication.getName();

        Query query = new Query();
        query.addCriteria(Criteria.where("isPublished").is(true)
                .orOperator(
                        Criteria.where("visibilities").in(visibilities),
                        Criteria.where("createdBy").is(username)
                )
        );
        if (CollectionUtils.isNotEmpty(journeyIDs)) {
            query.addCriteria(Criteria.where("id").in(journeyIDs));
        }
        if (CollectionUtils.isNotEmpty(cities)) {
            query.addCriteria(Criteria.where("city").in(cities));
        }
        if (CollectionUtils.isNotEmpty(countries)) {
            query.addCriteria(Criteria.where("country").in(countries));
        }
        if (CollectionUtils.isNotEmpty(years)) {
            query.addCriteria(
                    Criteria.where("$expr").is(new Document("$in", List.of(new Document("$year", "$journeyDate"), years)))
            );
        }
        if (Boolean.TRUE.equals(today)) {
            Criteria monthCriteria = Criteria.where("$expr").is(new Document("$eq", List.of(new Document("$month", "$journeyDate"), LocalDate.now().getMonthValue())));
            Criteria dayCriteria = Criteria.where("$expr").is(new Document("$eq", List.of(new Document("$dayOfMonth", "$journeyDate"), LocalDate.now().getDayOfMonth())));
            query.addCriteria(monthCriteria.andOperator(dayCriteria));
        }
        if (Boolean.TRUE.equals(upcoming)) {
            query.addCriteria(Criteria.where("journeyDate").gt(LocalDate.now()).lte(LocalDate.now().plusDays(DAYS_FOR_UPCOMING_TIMELINE)));
        }

        List<JourneyEntity> entities = mongoTemplate.find(query, JourneyEntity.class);


        return TimelineDataTransformer.transform(entities, journeyIDs, cities, countries, years, today, upcoming);
    }


}
