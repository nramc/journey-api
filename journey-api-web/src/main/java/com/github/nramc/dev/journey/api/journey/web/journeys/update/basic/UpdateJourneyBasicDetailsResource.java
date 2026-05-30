package com.github.nramc.dev.journey.api.journey.web.journeys.update.basic;

import com.github.nramc.dev.journey.api.journey.domain.Journey;
import com.github.nramc.dev.journey.api.journey.repository.JourneyEntity;
import com.github.nramc.dev.journey.api.journey.repository.JourneyRepository;
import com.github.nramc.dev.journey.api.journey.repository.converter.JourneyConverter;
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

import static com.github.nramc.dev.journey.api.shared.web.Resources.MediaType.UPDATE_JOURNEY_BASIC_DETAILS;
import static com.github.nramc.dev.journey.api.shared.web.Resources.UPDATE_JOURNEY;

@RestController
@Slf4j
@RequiredArgsConstructor
class UpdateJourneyBasicDetailsResource {
    private final JourneyRepository journeyRepository;

    @RestDocCommonResponse
    @Operation(summary = "Update Basic details of Journey", tags = {"Update Journey"})
    @ApiResponse(responseCode = "200", description = "Journey details updated successfully")
    @PutMapping(value = UPDATE_JOURNEY, consumes = UPDATE_JOURNEY_BASIC_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Journey> updateBasicDetails(@RequestBody @Valid UpdateJourneyBasicDetailsRequest request, @PathVariable String id) {
        JourneyEntity entity = journeyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Given ID does not exists, can't update base info"));

        JourneyEntity journey = UpdateJourneyMemoriesDetailsConverter.extendDetailsWith(request, entity);

        JourneyEntity journeyEntity = journeyRepository.save(journey);

        log.info("Journey's base details saved successfully with id:{}", journeyEntity.getId());
        return ResponseEntity.status(HttpStatus.OK).body(JourneyConverter.convert(journeyEntity));
    }
}
