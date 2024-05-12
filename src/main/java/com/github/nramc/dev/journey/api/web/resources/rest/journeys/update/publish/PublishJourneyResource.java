package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.publish;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.dto.Journey;
import com.github.nramc.dev.journey.api.web.dto.converter.JourneyConverter;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.UpdateJourneyConverter;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.validator.JourneyValidator;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.MediaType.PUBLISH_JOURNEY_DETAILS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_JOURNEY;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Update Journey Details Resource")
public class PublishJourneyResource {
    private final JourneyRepository journeyRepository;
    private final JourneyValidator journeyValidator;

    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "Journey details updated successfully")
    @PutMapping(value = UPDATE_JOURNEY, consumes = PUBLISH_JOURNEY_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Journey> publishJourney(
            @RequestBody @Valid PublishJourneyRequest request,
            @PathVariable String id) {
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
