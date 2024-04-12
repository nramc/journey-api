package com.github.nramc.dev.journey.api.web.resources.rest.find;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.web.dto.Journey;
import com.github.nramc.dev.journey.api.web.dto.converter.JourneyConverter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.Resources.FIND_JOURNEY;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class GetJourneyByIdResource {
    private final JourneyRepository journeyRepository;

    @GetMapping(value = FIND_JOURNEY, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Journey> findAndReturnJson(@Valid @NotBlank @PathVariable String id) {

        Optional<JourneyEntity> entityOptional = journeyRepository.findById(id);
        Optional<Journey> findJourneyResponse = entityOptional.map(JourneyConverter::convert);

        log.info("Journey exists? [{}]", findJourneyResponse.isPresent());
        return ResponseEntity.of(findJourneyResponse);
    }

}
