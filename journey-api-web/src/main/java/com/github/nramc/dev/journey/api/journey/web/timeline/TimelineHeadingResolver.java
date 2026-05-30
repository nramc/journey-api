package com.github.nramc.dev.journey.api.journey.web.timeline;

import com.github.nramc.dev.journey.api.journey.domain.Journey;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public final class TimelineHeadingResolver {

    public static final String DEFAULT_HEADING = "Timeline";

    private TimelineHeadingResolver() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @SuppressWarnings("java:S107") // Utility method, project style allows >7 params
    public static String resolve(
            List<String> ids,
            List<String> cities,
            List<String> countries,
            List<String> categories,
            List<Long> years,
            Boolean today,
            Integer upcomingDays,
            List<Journey> journeys) {
        if (Boolean.TRUE.equals(today)) {
            return "Today in History";
        } else if (upcomingDays != null) {
            return "Upcoming Journiversaries";
        } else if (CollectionUtils.isNotEmpty(years)) {
            return resolveYearHeading(years);
        } else if (CollectionUtils.isNotEmpty(ids)) {
            return resolveJourneyIdsHeading(journeys);
        } else if (CollectionUtils.isNotEmpty(cities)) {
            return resolveCityHeading(cities);
        } else if (CollectionUtils.isNotEmpty(countries)) {
            return resolveCountryHeading(countries);
        } else if (CollectionUtils.isNotEmpty(categories)) {
            return resolveCategoryHeading(categories);
        } else {
            return DEFAULT_HEADING;
        }
    }

    private static String resolveYearHeading(List<Long> years) {
        if (CollectionUtils.isEmpty(years)) {
            return DEFAULT_HEADING;
        }
        if (CollectionUtils.size(years) == 1) {
            return String.valueOf(years.getFirst());
        }
        return years.getFirst() + " - " + years.getLast();
    }

    private static String resolveJourneyIdsHeading(List<Journey> journeys) {
        if (CollectionUtils.size(journeys) == 1) {
            return journeys.getFirst().name();
        }
        return "Journeys";
    }

    private static String resolveCityHeading(List<String> cities) {
        if (CollectionUtils.size(cities) == 1) {
            return cities.getFirst();
        }
        return "Cities";
    }

    private static String resolveCountryHeading(List<String> countries) {
        if (CollectionUtils.size(countries) == 1) {
            return countries.getFirst();
        }
        return "Countries";
    }

    private static String resolveCategoryHeading(List<String> categories) {
        if (CollectionUtils.size(categories) == 1) {
            return categories.getFirst();
        }
        return "Categories";
    }

}
