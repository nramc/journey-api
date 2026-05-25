package com.github.nramc.dev.journey.api.journey.web.journeys.find;

import com.github.nramc.dev.journey.api.journey.domain.security.JourneyAuthorizationManager;
import com.github.nramc.dev.journey.api.journey.repository.JourneyEntity;
import com.github.nramc.dev.journey.api.journey.repository.JourneyRepository;
import com.github.nramc.dev.journey.api.journey.repository.converter.JourneyFeatureConverter;
import com.github.nramc.geojson.domain.Feature;
import com.github.nramc.geojson.domain.FeatureCollection;
import com.github.nramc.geojson.domain.GeoJson;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.github.nramc.dev.journey.api.shared.web.Resources.FIND_PUBLISHED_JOURNEYS;
import static com.github.nramc.dev.journey.api.shared.web.Resources.MediaType.JOURNEYS_GEO_JSON;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FindPublishedJourneyResource {
    private final JourneyRepository journeyRepository;

    @Operation(summary = "Find all published Journeys and return result as GeoJSON", tags = {"Search Journey"})
    @GetMapping(value = FIND_PUBLISHED_JOURNEYS, produces = JOURNEYS_GEO_JSON)
    public GeoJson find(
            Authentication authentication
    ) {
        List<JourneyEntity> entities = journeyRepository.findAllByIsPublished(true);

        List<Feature> features = entities.stream()
                .filter(entity -> JourneyAuthorizationManager.isAuthorized(entity, authentication))
                .map(JourneyFeatureConverter::toFeature).toList();
        log.info("Journeys:[{}] Features:[{}]", entities.size(), features.size());

        return FeatureCollection.of(features);
    }

}
