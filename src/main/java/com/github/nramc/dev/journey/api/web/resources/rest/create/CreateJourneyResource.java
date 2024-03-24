package com.github.nramc.dev.journey.api.web.resources.rest.create;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.github.nramc.dev.journey.api.web.resources.Resources.CREATE_JOURNEY;

@RestController
@Slf4j
public class CreateJourneyResource {

    @PostMapping(value = CREATE_JOURNEY, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@RequestBody CreateJourneyRequest request) {
        log.info("Jouney {}", request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
