package com.github.nramc.dev.journey.api.web.resources.rest.journeys.create;

import com.github.nramc.dev.journey.api.core.journey.security.Visibility;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.user.AuthUser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Set;

import static com.github.nramc.dev.journey.api.core.journey.security.Visibility.MYSELF;


class CreateJourneyConverter {
    private static final Set<Visibility> DEFAULT_VISIBILITIES = Set.of(MYSELF);

    public static JourneyEntity convert(CreateJourneyRequest request, AuthUser authUser) {
        return JourneyEntity.builder()
                .id(null)
                .name(request.name())
                .description(request.description())
                .tags(CollectionUtils.emptyIfNull(request.tags()).stream().map(StringUtils::lowerCase).toList())
                .thumbnail(request.thumbnail())
                .journeyDate(request.journeyDate())
                .createdDate(LocalDate.now())
                .createdBy(authUser.getUsername())
                .visibilities(DEFAULT_VISIBILITIES)
                .isPublished(false)
                .build();

    }

    static CreateJourneyResponse convert(JourneyEntity entity) {
        return CreateJourneyResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .tags(entity.getTags())
                .thumbnail(entity.getThumbnail())
                .createdDate(entity.getCreatedDate())
                .journeyDate(entity.getJourneyDate())
                .build();
    }

    private CreateJourneyConverter() {
        throw new IllegalStateException("Utility class");
    }

}
