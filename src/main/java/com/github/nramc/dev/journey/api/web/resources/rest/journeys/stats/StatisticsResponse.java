package com.github.nramc.dev.journey.api.web.resources.rest.journeys.stats;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record StatisticsResponse(
        List<KeyValueStatistics> categories,
        List<KeyValueStatistics> cities,
        List<KeyValueStatistics> countries,
        List<KeyValueStatistics> years
) {

    public record KeyValueStatistics(String name, Long count) {
    }

}
