package com.github.nramc.dev.journey.api.journey.web.journeys.create;

import com.github.nramc.dev.journey.api.journey.repository.JourneyEntity;
import com.github.nramc.dev.journey.api.journey.repository.JourneyRepository;
import com.github.nramc.dev.journey.api.shared.web.doc.RestDocCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.shared.web.Resources.NEW_JOURNEY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Create Journey")
class CreateJourneyResource {
    private final JourneyRepository journeyRepository;

    @Operation(summary = "Create new Journey with basic details")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "201", description = "Journey created successfully",
            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreateJourneyResponse.class))})
    @PostMapping(value = NEW_JOURNEY, consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<CreateJourneyResponse> create(
            Authentication authentication,
            @RequestBody @Valid CreateJourneyRequest request) {
        JourneyEntity entity = CreateJourneyConverter.convert(request, authentication.getName());

        JourneyEntity journeyEntity = journeyRepository.save(entity);

        log.info("new Journey saved successfully with id:{}", journeyEntity.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CreateJourneyConverter.convert(journeyEntity));
    }

}
