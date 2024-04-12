package com.github.nramc.dev.journey.api.web.resources.rest.update;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.dto.Journey;
import com.github.nramc.dev.journey.api.web.dto.converter.JourneyConverter;
import com.github.nramc.dev.journey.api.web.resources.rest.update.validator.JourneyValidator;
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

import static com.github.nramc.dev.journey.api.web.resources.Resources.MediaType.PUBLISH_JOURNEY_DETAILS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MediaType.UPDATE_JOURNEY_IMAGES_DETAILS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.MediaType.UPDATE_JOURNEY_VIDEOS_DETAILS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_JOURNEY;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class UpdateJourneyResource {
    private final JourneyRepository journeyRepository;
    private final JourneyValidator journeyValidator;


    @PutMapping(value = UPDATE_JOURNEY, consumes = UPDATE_JOURNEY_IMAGES_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Journey> updateImagesDetails(@RequestBody @Valid UpdateJourneyImagesDetailsRequest request, @PathVariable String id) {
        JourneyEntity entity = journeyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Given ID does not exists, can't update images info"));

        JourneyEntity journey = UpdateJourneyConverter.extendWithImagesDetails(request, entity);

        JourneyEntity journeyEntity = journeyRepository.save(journey);

        log.info("Journey's image information saved successfully with id:{}", journeyEntity.getId());
        return ResponseEntity.status(HttpStatus.OK).body(JourneyConverter.convert(journeyEntity));
    }

    @PutMapping(value = UPDATE_JOURNEY, consumes = UPDATE_JOURNEY_VIDEOS_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Journey> updateVideosDetails(@RequestBody @Valid UpdateJourneyVideosDetailsRequest request, @PathVariable String id) {
        JourneyEntity entity = journeyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Given ID does not exists, can't update videos info"));

        JourneyEntity journey = UpdateJourneyConverter.extendWithVideosDetails(request, entity);

        JourneyEntity journeyEntity = journeyRepository.save(journey);

        log.info("Journey's video information saved successfully with id:{}", journeyEntity.getId());
        return ResponseEntity.status(HttpStatus.OK).body(JourneyConverter.convert(journeyEntity));
    }

    @PutMapping(value = UPDATE_JOURNEY, consumes = PUBLISH_JOURNEY_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Journey> publishJourney(@RequestBody @Valid PublishJourneyRequest request, @PathVariable String id) {
        JourneyEntity entity = journeyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Given ID does not exists, can't publish journey"));

        JourneyEntity journey = UpdateJourneyConverter.extendEntityWith(request, entity);
        Journey journeyToBePublished = JourneyConverter.convert(journey);

        boolean canPublish = journeyValidator.canPublish(journeyToBePublished);

        if (canPublish) {
            JourneyEntity journeyEntity = journeyRepository.save(journey);
            log.info("Journey published successfully with id:{}", journeyEntity.getId());
            return ResponseEntity.status(HttpStatus.OK).body(JourneyConverter.convert(journeyEntity));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

}
