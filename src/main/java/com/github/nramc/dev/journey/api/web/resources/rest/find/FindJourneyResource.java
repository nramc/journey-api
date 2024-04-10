package com.github.nramc.dev.journey.api.web.resources.rest.find;

import com.github.nramc.commons.geojson.domain.Feature;
import com.github.nramc.commons.geojson.domain.FeatureCollection;
import com.github.nramc.commons.geojson.domain.GeoJson;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.dto.Journey;
import com.github.nramc.dev.journey.api.web.dto.converter.JourneyConverter;
import com.github.nramc.dev.journey.api.web.dto.converter.JourneyFeatureConverter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEY;
import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEYS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MediaType.JOURNEYS_GEO_JSON;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class FindJourneyResource {
    private final JourneyRepository journeyRepository;

    @GetMapping(value = FIND_JOURNEY, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Journey> findAndReturnJson(@Valid @NotBlank @PathVariable String id) {

        Optional<JourneyEntity> entityOptional = journeyRepository.findById(id);
        Optional<Journey> findJourneyResponse = entityOptional.map(JourneyConverter::convert);

        log.info("Journey exists? [{}]", findJourneyResponse.isPresent());
        return ResponseEntity.of(findJourneyResponse);
    }

    @GetMapping(value = FIND_JOURNEYS, produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Journey> findAllAndReturnJson(
            @RequestParam(name = "pageIndex", defaultValue = "0") int pageIndex,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort", defaultValue = "createdDate") String sortColumn,
            @RequestParam(name = "order", defaultValue = "DESC") Sort.Direction sortOrder) {

        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(sortOrder, sortColumn));

        Page<JourneyEntity> entityPage = journeyRepository.findAll(pageable);
        Page<Journey> responsePage = entityPage.map(JourneyConverter::convert);

        log.info("Journey exists:[{}] pages:[{}] total:[{}]",
                responsePage.hasContent(), responsePage.getTotalPages(), responsePage.getTotalElements());
        return responsePage;
    }

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
