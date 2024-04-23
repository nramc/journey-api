package com.github.nramc.dev.journey.api.web.resources.rest.create;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.security.Visibility;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.Set;

import static com.github.nramc.dev.journey.api.security.Visibility.ADMINISTRATOR;
import static com.github.nramc.dev.journey.api.security.Visibility.MYSELF;


@UtilityClass
class CreateJourneyConverter {
    private static final Set<Visibility> DEFAULT_VISIBILITIES = Set.of(MYSELF, ADMINISTRATOR);

    public static JourneyEntity convert(CreateJourneyRequest request, AuthUser authUser) {
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
                .createdBy(authUser.getUsername())
                .visibilities(DEFAULT_VISIBILITIES)
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
