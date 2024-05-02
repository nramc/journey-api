package com.github.nramc.dev.journey.api.web.resources.rest.journeys.find;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.security.JourneyAuthorizationManager;
import com.github.nramc.dev.journey.api.web.dto.Journey;
import com.github.nramc.dev.journey.api.web.dto.converter.JourneyConverter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEY_BY_ID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FindJourneyByIdResource {
    private final JourneyRepository journeyRepository;

    @GetMapping(value = FIND_JOURNEY_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
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
