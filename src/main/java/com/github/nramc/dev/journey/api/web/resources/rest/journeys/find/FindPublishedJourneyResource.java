package com.github.nramc.dev.journey.api.web.resources.rest.journeys.find;

import com.github.nramc.commons.geojson.domain.Feature;
import com.github.nramc.commons.geojson.domain.FeatureCollection;
import com.github.nramc.commons.geojson.domain.GeoJson;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.security.JourneyAuthorizationManager;
import com.github.nramc.dev.journey.api.web.dto.converter.JourneyFeatureConverter;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_PUBLISHED_JOURNEYS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MediaType.JOURNEYS_GEO_JSON;

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
        JourneyEntity journeyEntity = new JourneyEntity();
        journeyEntity.setIsPublished(true);

        Example<JourneyEntity> journeyExample = Example.of(journeyEntity);
        List<JourneyEntity> entities = journeyRepository.findAll(journeyExample);

        List<Feature> features = entities.stream()
                .filter(entity -> JourneyAuthorizationManager.isAuthorized(entity, authentication))
                .map(JourneyFeatureConverter::toFeature).toList();
        log.info("Journeys:[{}] Features:[{}]", entities.size(), features.size());

        return FeatureCollection.of(features);
    }

}
