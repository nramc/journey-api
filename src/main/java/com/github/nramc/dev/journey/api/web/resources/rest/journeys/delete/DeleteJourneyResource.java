package com.github.nramc.dev.journey.api.web.resources.rest.journeys.delete;

import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.config.security.JourneyAuthorizationManager;
import com.github.nramc.dev.journey.api.core.services.cloudinary.CloudinaryService;
import com.github.nramc.dev.journey.api.web.dto.Journey;
import com.github.nramc.dev.journey.api.web.dto.converter.JourneyConverter;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.DELETE_JOURNEY;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Delete Journey Details Resource")
public class DeleteJourneyResource {
    private final JourneyRepository journeyRepository;
    private final CloudinaryService cloudinaryService;

    @RestDocCommonResponse
    @ApiResponse(responseCode = "200", description = "Journey details deleted successfully")
    @DeleteMapping(value = DELETE_JOURNEY, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteJourney(
            @PathVariable String id,
            Authentication authentication) {
        journeyRepository.findById(id)
                .filter(journeyEntity -> JourneyAuthorizationManager.isAuthorized(journeyEntity, authentication))
                .map(JourneyConverter::convert)
                .ifPresent(this::deleteJourney);
    }

    private void deleteJourney(Journey journey) {
        cloudinaryService.deleteJourney(journey);
        journeyRepository.deleteById(journey.id());
    }

}
