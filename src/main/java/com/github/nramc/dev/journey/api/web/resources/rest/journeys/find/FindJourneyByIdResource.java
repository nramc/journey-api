package com.github.nramc.dev.journey.api.web.resources.rest.journeys.find;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.core.journey.security.JourneyAuthorizationManager;
import com.github.nramc.dev.journey.api.web.dto.Journey;
import com.github.nramc.dev.journey.api.web.dto.converter.JourneyConverter;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEY_BY_ID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FindJourneyByIdResource {
    private final JourneyRepository journeyRepository;

    @Operation(summary = "Find Journey for given ID if exists, else throw error.", tags = {"Search Journey"})
    @RestDocCommonResponse
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Journey created successfully",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = Journey.class))}),
            @ApiResponse(responseCode = "404", description = "Journey not exists",
                    content = {@Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))})
    })
    @GetMapping(value = FIND_JOURNEY_BY_ID, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Journey> find(
            @Valid @NotBlank @PathVariable String id,
            Authentication authentication
    ) {

        Optional<JourneyEntity> entityOptional = journeyRepository.findById(id);
        Optional<Journey> findJourneyResponse = entityOptional
                .filter(entity -> JourneyAuthorizationManager.isAuthorized(entity, authentication))
                .map(JourneyConverter::convert);

        log.info("Journey exists? [{}]", findJourneyResponse.isPresent());
        return ResponseEntity.of(findJourneyResponse);
    }

}
