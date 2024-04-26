package com.github.nramc.dev.journey.api.web.resources.rest.create;

import com.github.nramc.dev.journey.api.repository.auth.AuthUser;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import com.github.nramc.dev.journey.api.services.AuthUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.github.nramc.dev.journey.api.web.resources.Resources.NEW_JOURNEY;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class CreateJourneyResource {
    private final JourneyRepository journeyRepository;
    private final AuthUserDetailsService userDetailsService;

    @PostMapping(value = NEW_JOURNEY, consumes = MediaType.APPLICATION_JSON_VALUE)
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
