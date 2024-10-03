package com.github.nramc.dev.journey.api.web.resources.rest.journeys.update.validator;

import com.github.nramc.dev.journey.api.core.journey.Journey;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class JourneyValidator {
    private final Validator validator;

    public boolean canPublish(Journey journey) {
        Set<ConstraintViolation<Journey>> violations = validator.validate(journey);
        log.info("Journey can not be published due to violations. {}", violations);
        return violations.isEmpty();
    }

}
