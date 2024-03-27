package com.github.nramc.dev.journey.api.web.resources.rest.create;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
class CreateJourneyConverter {

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
                .journeyDate(request.journeyDate())
                .createdDate(LocalDate.now())
                .build();

    }

    static CreateJourneyResponse convert(JourneyEntity entity) {
        return CreateJourneyResponse.builder()
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
                .createdDate(entity.getCreatedDate())
                .journeyDate(entity.getJourneyDate())
                .build();

    }

}
