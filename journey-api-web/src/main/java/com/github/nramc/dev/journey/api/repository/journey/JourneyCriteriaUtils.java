package com.github.nramc.dev.journey.api.repository.journey;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

@SuppressWarnings("java:S1192") // Suppressed String literals should not be repeated
public final class JourneyCriteriaUtils {

    static Criteria getCriteriaForUpcomingAnniversary(int daysAhead) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysAhead);

        if (startDate.getMonthValue() == endDate.getMonthValue()) {
            return getCriteriaWhenDateRangeFallsUnderSameMonths(startDate, endDate);
        } else {
            return getCriteriaWhenDateRangeFallsCrossMonths(startDate, endDate);
        }
    }

    private static Criteria getCriteriaWhenDateRangeFallsCrossMonths(LocalDate startDate, LocalDate endDate) {
        List<Criteria> criteriaList = new ArrayList<>();
        // Start of the month
        criteriaList.add(new Criteria().andOperator(
                Criteria.where("$expr").is(new Document("eq", List.of(new Document("$month", "$journeyDate"), startDate.getMonthValue()))),
                Criteria.where("$expr").is(new Document("$gte", List.of(new Document("$dayOfMonth", "$journeyDate"), startDate.getDayOfMonth())))
        ));

        // Month in between
        IntStream.range(startDate.getMonthValue() + 1, endDate.getMonthValue()).forEach(month ->
                criteriaList.add(Criteria.where("$expr").is(new Document("$eq", List.of(new Document("$month", "$journeyDate"), month))))
        );

        // End of the month
        criteriaList.add(new Criteria().andOperator(
                Criteria.where("$expr").is(new Document("$eq", List.of(new Document("$month", "$journeyDate"), endDate.getMonthValue()))),
                Criteria.where("$expr").is(new Document("$lte", List.of(new Document("$dayOfMonth", "$journeyDate"), endDate.getDayOfMonth())))
        ));
        return new Criteria().orOperator(criteriaList);
    }

    private static Criteria getCriteriaWhenDateRangeFallsUnderSameMonths(LocalDate startDate, LocalDate endDate) {
        return new Criteria().andOperator(
                Criteria.where("$expr").is(new Document("$eq", List.of(new Document("$month", "$journeyDate"), startDate.getMonthValue()))),
                Criteria.where("$expr").is(new Document("$gte", List.of(new Document("$dayOfMonth", "$journeyDate"), startDate.getDayOfMonth()))),
                Criteria.where("$expr").is(new Document("$lte", List.of(new Document("$dayOfMonth", "$journeyDate"), endDate.getDayOfMonth())))
        );
    }

    static Criteria transformSearchCriteria(JourneySearchCriteria searchCriteria) {
        List<Criteria> criteriaList = new ArrayList<>();

        criteriaList.add(
                new Criteria().orOperator(
                        Criteria.where("createdBy").is(searchCriteria.appUser().username()),
                        Criteria.where("visibilities").in(searchCriteria.visibilities())
                )
        );
        criteriaList.add(Criteria.where("isPublished").in(searchCriteria.publishedFlags()));
        if (StringUtils.isNotEmpty(searchCriteria.searchText())) {
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("name").regex(compile(".*" + searchCriteria.searchText() + ".*", CASE_INSENSITIVE)),
                    Criteria.where("description").regex(compile(".*" + searchCriteria.searchText() + ".*", CASE_INSENSITIVE))
            ));
        }
        Optional.ofNullable(searchCriteria.ids()).filter(CollectionUtils::isNotEmpty)
                .ifPresent(ids -> criteriaList.add(Criteria.where("id").in(ids)));
        Optional.ofNullable(searchCriteria.tags()).filter(CollectionUtils::isNotEmpty)
                .ifPresent(tags -> criteriaList.add(Criteria.where("tags").in(tags)));
        Optional.ofNullable(searchCriteria.cities()).filter(CollectionUtils::isNotEmpty)
                .ifPresent(cities -> criteriaList.add(Criteria.where("geoDetails.city").in(cities)));
        Optional.ofNullable(searchCriteria.countries()).filter(CollectionUtils::isNotEmpty)
                .ifPresent(countries -> criteriaList.add(Criteria.where("geoDetails.country").in(countries)));
        Optional.ofNullable(searchCriteria.categories()).filter(CollectionUtils::isNotEmpty)
                .ifPresent(categories -> criteriaList.add(Criteria.where("geoDetails.category").in(categories)));
        Optional.ofNullable(searchCriteria.journeyDateFrom())
                .ifPresent(startDate -> criteriaList.add(Criteria.where("journeyDate").gte(startDate)));
        Optional.ofNullable(searchCriteria.journeyDateUpTo())
                .ifPresent(endDate -> criteriaList.add(Criteria.where("journeyDate").lte(endDate)));
        Optional.ofNullable(searchCriteria.journeyYears()).filter(CollectionUtils::isNotEmpty)
                .ifPresent(years -> criteriaList.add(Criteria.where("$expr").is(new Document("$in", List.of(new Document("$year", "$journeyDate"), years)))));
        Optional.ofNullable(searchCriteria.journeyDaysUpTo())
                .ifPresent(daysUpto -> criteriaList.add(getCriteriaForUpcomingAnniversary(daysUpto)));

        return new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
    }

    private JourneyCriteriaUtils() {
        throw new IllegalStateException("Utility class");
    }

}
