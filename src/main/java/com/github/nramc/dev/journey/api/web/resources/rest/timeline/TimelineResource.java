package com.github.nramc.dev.journey.api.web.resources.rest.timeline;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyExtendedEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImageDetailEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyImagesDetailsEntity;
import com.github.nramc.dev.journey.api.security.Visibility;
import com.github.nramc.dev.journey.api.security.utils.AuthUtils;
import com.github.nramc.dev.journey.api.web.resources.rest.timeline.TimelineData.TimelineImage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.nramc.dev.journey.api.web.resources.Resources.GET_TIMELINE_DATA;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Timeline Resource")
public class TimelineResource {
    private final MongoTemplate mongoTemplate;

    @Operation(summary = "Get Timeline data")
    @GetMapping(value = GET_TIMELINE_DATA, produces = APPLICATION_JSON_VALUE)
    public TimelineData getTimelineData(Authentication authentication) {
        Set<Visibility> visibilities = AuthUtils.getVisibilityFromAuthority(authentication.getAuthorities());
        String username = authentication.getName();

        Query query = new Query();
        query.addCriteria(Criteria.where("isPublished").is(true)
                .orOperator(
                        Criteria.where("visibilities").in(visibilities),
                        Criteria.where("createdBy").is(username)
                )
        );


        List<JourneyEntity> entities = mongoTemplate.find(query, JourneyEntity.class);


        return TimelineData.builder()
                .heading("timeline-heading")
                .title("timeline-title")
                .images(getFavoriteImagesForTimeline(entities))
                .build();
    }

    private static List<TimelineImage> getFavoriteImagesForTimeline(List<JourneyEntity> entities) {
        return CollectionUtils.emptyIfNull(entities).stream()
                .map(JourneyEntity::getExtended)
                .map(JourneyExtendedEntity::getImagesDetails)
                .map(JourneyImagesDetailsEntity::getImages)
                .flatMap(Collection::stream)
                .filter(JourneyImageDetailEntity::isFavorite)
                .map(TimelineResource::toTimelineImage)
                .toList();
    }

    private static TimelineImage toTimelineImage(JourneyImageDetailEntity entity) {
        return TimelineImage.builder()
                .src(entity.getUrl())
                .caption(entity.getTitle())
                .args(Map.of())
                .build();
    }
}
