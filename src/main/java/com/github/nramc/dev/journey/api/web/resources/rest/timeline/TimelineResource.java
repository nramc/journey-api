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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.github.nramc.dev.journey.api.web.resources.Resources.GET_TIMELINE_DATA;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Timeline Resource")
public class TimelineResource {
    private final MongoTemplate mongoTemplate;

    @Operation(summary = "Get Timeline data")
    @GetMapping(value = GET_TIMELINE_DATA, produces = APPLICATION_JSON_VALUE)
    @SuppressWarnings("java:S1192")
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
        if (CollectionUtils.isNotEmpty(categories)) {
            query.addCriteria(Criteria.where("category").in(categories));
        }
        if (CollectionUtils.isNotEmpty(years)) {
            query.addCriteria(
                    Criteria.where("$expr").is(new Document("$in", List.of(new Document("$year", "$journeyDate"), years)))
            );
        }
        if (Boolean.TRUE.equals(today)) {
            LocalDateTime localDateTime = LocalDate.now().atStartOfDay();
            Criteria monthCriteria = Criteria.where("$expr").is(new Document("$eq", List.of(new Document("$month", "$journeyDate"), localDateTime.getMonthValue())));
            Criteria dayCriteria = Criteria.where("$expr").is(new Document("$eq", List.of(new Document("$dayOfMonth", "$journeyDate"), localDateTime.getDayOfMonth())));
            query.addCriteria(monthCriteria.andOperator(dayCriteria));
        }
        if (upcomingJourneysTillDays != null) {
            query.addCriteria(getUpcomingDaysCriteria(upcomingJourneysTillDays));
        }

        List<JourneyEntity> entities = mongoTemplate.find(query, JourneyEntity.class);


        return TimelineDataTransformer.transform(entities, journeyIDs, cities, countries, categories, years, today,
                upcomingJourneysTillDays != null);
    }

    private Criteria getUpcomingDaysCriteria(int upcomingJourneysTillDays) {
        Criteria criteria;
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(upcomingJourneysTillDays);

        if (startDate.getMonthValue() == endDate.getMonthValue()) {
            // Same month, just compare days
            criteria = new Criteria().andOperator(
                    Criteria.where("$expr").is(new Document("$gte", List.of(new Document("$dayOfMonth", "$journeyDate"), startDate.getDayOfMonth()))),
                    Criteria.where("$expr").is(new Document("$lte", List.of(new Document("$dayOfMonth", "$journeyDate"), endDate.getDayOfMonth()))),
                    Criteria.where("$expr").is(new Document("$eq", List.of(new Document("$month", "$journeyDate"), startDate.getMonthValue())))
            );
        } else {
            // Different months, compare both months and days
            criteria = new Criteria().orOperator(
                    new Criteria().andOperator(
                            Criteria.where("$expr").is(new Document("$gte", List.of(new Document("$dayOfMonth", "$journeyDate"), startDate.getDayOfMonth()))),
                            Criteria.where("$expr").is(new Document("$eq", List.of(new Document("$month", "$journeyDate"), startDate.getMonthValue())))
                    ),
                    new Criteria().andOperator(
                            Criteria.where("$expr").is(new Document("$lte", List.of(new Document("$dayOfMonth", "$journeyDate"), endDate.getDayOfMonth()))),
                            Criteria.where("$expr").is(new Document("$eq", List.of(new Document("$month", "$journeyDate"), endDate.getMonthValue())))
                    )
            );
        }
        return criteria;
    }


}
