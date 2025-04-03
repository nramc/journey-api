package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.basic;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

final class UpdateJourneyMemoriesDetailsConverter {

    static JourneyEntity extendDetailsWith(UpdateJourneyBasicDetailsRequest fromRequest, JourneyEntity toEntity) {
        return toEntity.toBuilder()
                .name(fromRequest.name())
                .description(fromRequest.description())
                .tags(CollectionUtils.emptyIfNull(fromRequest.tags()).stream().map(StringUtils::lowerCase).toList())
                .thumbnail(fromRequest.thumbnail())
                .journeyDate(fromRequest.journeyDate())
                .build();

    }

    private UpdateJourneyMemoriesDetailsConverter() {
        throw new IllegalStateException("Utility class");
    }
}
