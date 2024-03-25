package com.github.nramc.dev.journey.api.web.resources.rest.find;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
class FindJourneyConverter {

    static FindJourneyResponse convert(JourneyEntity entity) {
        return FindJourneyResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .city(entity.getCity())
                .country(entity.getCountry())
                .category(entity.getCategory())
                .tags(entity.getTags())
                .location(entity.getLocation())
                .thumbnail(entity.getThumbnail())
                .build();
    }

}
