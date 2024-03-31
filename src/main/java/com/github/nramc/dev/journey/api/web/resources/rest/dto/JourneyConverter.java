package com.github.nramc.dev.journey.api.web.resources.rest.dto;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JourneyConverter {

    public static JourneyResponse convert(JourneyEntity entity) {
        return JourneyResponse.builder()
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
                .journeyDate(entity.getJourneyDate())
                .createdDate(entity.getCreatedDate())
                .build();
    }

}
