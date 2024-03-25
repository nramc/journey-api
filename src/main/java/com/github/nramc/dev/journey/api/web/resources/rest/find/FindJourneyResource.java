package com.github.nramc.dev.journey.api.web.resources.rest.find;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEY;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FindJourneyResource {
    private final JourneyRepository journeyRepository;

    @GetMapping(value = FIND_JOURNEY, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FindJourneyResponse> findAndReturnJson(@Valid @NotBlank @PathVariable String id) {

        Optional<JourneyEntity> entityOptional = journeyRepository.findById(id);
        Optional<FindJourneyResponse> findJourneyResponse = entityOptional.map(FindJourneyConverter::convert);

        log.info("Journey exists? [{}]", findJourneyResponse.isPresent());
        return ResponseEntity.of(findJourneyResponse);
    }

}
