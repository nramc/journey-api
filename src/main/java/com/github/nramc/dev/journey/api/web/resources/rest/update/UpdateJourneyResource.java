package com.github.nramc.dev.journey.api.web.resources.rest.update;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.resources.rest.dto.Journey;
import com.github.nramc.dev.journey.api.web.resources.rest.dto.JourneyConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.MediaType.UPDATE_JOURNEY_BASIC_DETAILS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MediaType.UPDATE_JOURNEY_GEO_DETAILS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_JOURNEY;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class UpdateJourneyResource {
    private final JourneyRepository journeyRepository;

    @PutMapping(value = UPDATE_JOURNEY, consumes = UPDATE_JOURNEY_BASIC_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Journey> updateBasicDetails(@RequestBody @Valid UpdateJourneyBasicDetailsRequest request, @PathVariable String id) {
        JourneyEntity entity = journeyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Given ID does not exists"));

        JourneyEntity journey = UpdateJourneyConverter.copyData(request, entity);

        JourneyEntity journeyEntity = journeyRepository.save(journey);

        log.info("Journey saved successfully with id:{}", journeyEntity.getId());
        return ResponseEntity.status(HttpStatus.OK).body(JourneyConverter.convert(journeyEntity));
    }

    @PutMapping(value = UPDATE_JOURNEY, consumes = UPDATE_JOURNEY_GEO_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Journey> updateGeoDetails(@RequestBody @Valid UpdateJourneyGeoDetailsRequest request, @PathVariable String id) {
        JourneyEntity entity = journeyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Given ID does not exists"));

        JourneyEntity journey = UpdateJourneyConverter.extendWithGeoDetails(request, entity);

        JourneyEntity journeyEntity = journeyRepository.save(journey);

        log.info("Journey saved successfully with id:{}", journeyEntity.getId());
        return ResponseEntity.status(HttpStatus.OK).body(JourneyConverter.convert(journeyEntity));
    }

}
