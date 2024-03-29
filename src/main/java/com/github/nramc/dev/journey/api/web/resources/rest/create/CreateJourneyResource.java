package com.github.nramc.dev.journey.api.web.resources.rest.create;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.CREATE_JOURNEY;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class CreateJourneyResource {
    private final JourneyRepository journeyRepository;

    @PostMapping(value = CREATE_JOURNEY, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateJourneyResponse> create(@RequestBody @Valid CreateJourneyRequest request) {
        JourneyEntity entity = CreateJourneyConverter.convert(request);

        JourneyEntity journeyEntity = journeyRepository.save(entity);

        log.info("new Journey saved successfully with id:{}", journeyEntity.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CreateJourneyConverter.convert(journeyEntity));
    }

}
