package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.images;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.dto.Journey;
import com.github.nramc.dev.journey.api.web.dto.converter.JourneyConverter;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.UpdateJourneyConverter;
import io.swagger.v3.oas.annotations.Operation;
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

import static com.github.nramc.dev.journey.api.web.resources.Resources.MediaType.UPDATE_JOURNEY_IMAGES_DETAILS;
import static com.github.nramc.dev.journey.api.web.resources.Resources.UPDATE_JOURNEY;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Update Journey Details Resource")
public class UpdateJourneyImagesDetailsResource {
    private final JourneyRepository journeyRepository;

    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "Journey details updated successfully")
    @PutMapping(value = UPDATE_JOURNEY, consumes = UPDATE_JOURNEY_IMAGES_DETAILS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Journey> updateImagesDetails(@RequestBody @Valid UpdateJourneyImagesDetailsRequest request, @PathVariable String id) {
        JourneyEntity entity = journeyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Given ID does not exists, can't update images info"));

        JourneyEntity journey = UpdateJourneyConverter.extendWithImagesDetails(request, entity);

        JourneyEntity journeyEntity = journeyRepository.save(journey);

        log.info("Journey's image information saved successfully with id:{}", journeyEntity.getId());
        return ResponseEntity.status(HttpStatus.OK).body(JourneyConverter.convert(journeyEntity));
    }
}
