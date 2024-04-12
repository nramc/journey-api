package com.github.nramc.dev.journey.api.web.resources.rest.find;

import com.github.nramc.commons.geojson.domain.Feature;
import com.github.nramc.commons.geojson.domain.FeatureCollection;
import com.github.nramc.commons.geojson.domain.GeoJson;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.dto.converter.JourneyFeatureConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEYS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MediaType.JOURNEYS_GEO_JSON;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class FindJourneyResource {
    private final JourneyRepository journeyRepository;


    @GetMapping(value = FIND_JOURNEYS, produces = JOURNEYS_GEO_JSON)
    public GeoJson findAllAndReturnGeoJson() {
        JourneyEntity journeyEntity = new JourneyEntity();
        journeyEntity.setIsPublished(true);

        Example<JourneyEntity> journeyExample = Example.of(journeyEntity);
        List<JourneyEntity> entities = journeyRepository.findAll(journeyExample);

        List<Feature> features = entities.stream().map(JourneyFeatureConverter::toFeature).toList();
        log.info("Journeys:[{}] Features:[{}]", entities.size(), features.size());

        return FeatureCollection.of(features);
    }

}
