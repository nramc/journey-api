package com.github.nramc.dev.journey.api.journey.web.journeys.update.publish;

import com.github.nramc.dev.journey.api.journey.domain.Journey;
import com.github.nramc.dev.journey.api.journey.repository.JourneyEntity;
import com.github.nramc.dev.journey.api.journey.repository.JourneyRepository;
import com.github.nramc.dev.journey.api.journey.repository.converter.JourneyConverter;
import com.github.nramc.dev.journey.api.journey.web.journeys.update.UpdateJourneyConverter;
import com.github.nramc.dev.journey.api.journey.web.journeys.update.validator.JourneyValidator;
import com.github.nramc.dev.journey.api.shared.web.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

import static com.github.nramc.dev.journey.api.shared.web.Resources.MediaType.PUBLISH_JOURNEY_DETAILS;
import static com.github.nramc.dev.journey.api.shared.web.Resources.UPDATE_JOURNEY;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PublishJourneyResource {
    private final JourneyRepository journeyRepository;
    private final JourneyValidator journeyValidator;

    @RestDocCommonResponse
    @Operation(summary = "Publish or draft Journey details", tags = {"Update Journey"})
    @ApiResponse(responseCode = "200", description = "Journey details updated successfully")
    @PutMapping(value = UPDATE_JOURNEY, consumes = PUBLISH_JOURNEY_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Journey> publishJourney(
            @RequestBody @Valid PublishJourneyRequest request,
            @PathVariable String id) {
        JourneyEntity entity = journeyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Given ID does not exists, can't publish journey"));

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
