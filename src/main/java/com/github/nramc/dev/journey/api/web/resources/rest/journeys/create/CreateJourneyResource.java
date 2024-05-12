package com.github.nramc.dev.journey.api.web.resources.rest.journeys.create;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.services.AuthUserDetailsService;
import com.github.nramc.dev.journey.api.web.resources.rest.doc.RestDocCommonResponse;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.Resources.NEW_JOURNEY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Create Journey")
public class CreateJourneyResource {
    private final JourneyRepository journeyRepository;
    private final AuthUserDetailsService userDetailsService;

    @Operation(summary = "Create new Journey with basic details")
    @RestDocCommonResponse
    @ApiResponse(responseCode = "201", description = "Journey created successfully",
            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreateJourneyResponse.class))})
    @PostMapping(value = NEW_JOURNEY, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateJourneyResponse> create(
            Authentication authentication,
            @RequestBody @Valid CreateJourneyRequest request) {
        AuthUser authUser = Optional.of(authentication)
                .map(Authentication::getName)
                .map(userDetailsService::loadUserByUsername)
                .map(AuthUser.class::cast)
                .orElseThrow(() -> new AccessDeniedException("User does not exists"));

        JourneyEntity entity = CreateJourneyConverter.convert(request, authUser);

        JourneyEntity journeyEntity = journeyRepository.save(entity);

        log.info("new Journey saved successfully with id:{}", journeyEntity.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CreateJourneyConverter.convert(journeyEntity));
    }

}
