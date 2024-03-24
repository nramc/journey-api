package com.github.nramc.dev.journey.api.web.resources.rest.create;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CreateRequestAndEntityConverter {

    public static JourneyEntity convert(CreateJourneyRequest request) {
        return JourneyEntity.builder()
                .id(null)
                .name(request.name())
                .title(request.title())
                .description(request.description())
                .city(request.city())
                .country(request.country())
                .category(request.category())
                .tags(request.tags())
                .location(request.location())
                .thumbnail(request.thumbnail())
                .build();

    }

}
