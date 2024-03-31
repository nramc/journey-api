package com.github.nramc.dev.journey.api.web.resources.rest.update;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UpdateJourneyConverter {

    public static JourneyEntity copyData(UpdateJourneyBasicDetailsRequest fromRequest, JourneyEntity toEntity) {
        return toEntity.toBuilder()
                .name(fromRequest.name())
                .title(fromRequest.title())
                .description(fromRequest.description())
                .city(fromRequest.city())
                .country(fromRequest.country())
                .category(fromRequest.category())
                .tags(fromRequest.tags())
                .location(fromRequest.location())
                .thumbnail(fromRequest.thumbnail())
                .journeyDate(fromRequest.journeyDate())
                .build();

    }
}
