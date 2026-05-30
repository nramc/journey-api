package com.github.nramc.dev.journey.api.journey.web.journeys.create;

import com.github.nramc.dev.journey.api.journey.repository.JourneyEntity;
import com.github.nramc.dev.journey.api.shared.domain.Visibility;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Set;

import static com.github.nramc.dev.journey.api.shared.domain.Visibility.MYSELF;


final class CreateJourneyConverter {
    private static final Set<Visibility> DEFAULT_VISIBILITIES = Set.of(MYSELF);

    public static JourneyEntity convert(CreateJourneyRequest request, String username) {
        return JourneyEntity.builder()
                .id(null)
                .name(request.name())
                .description(request.description())
                .tags(CollectionUtils.emptyIfNull(request.tags()).stream().map(StringUtils::lowerCase).toList())
                .thumbnail(request.thumbnail())
                .journeyDate(request.journeyDate())
                .createdDate(LocalDate.now())
                .createdBy(username)
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
