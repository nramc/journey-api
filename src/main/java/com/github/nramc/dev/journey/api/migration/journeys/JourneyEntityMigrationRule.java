package com.github.nramc.dev.journey.api.migration.journeys;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class JourneyEntityMigrationRule implements Runnable {
    private final JourneyRepository journeyRepository;

    @Override
    public void run() {
        List<JourneyEntity> journeys = journeyRepository.findAll();
        CollectionUtils.emptyIfNull(journeys).stream()
                .filter(journeyEntity -> Objects.nonNull(journeyEntity.getGeoDetails()))
                .forEach(this::migrateJourney);
    }

    private void migrateJourney(JourneyEntity journeyEntity) {
        JourneyEntity updatedEntity = journeyEntity.toBuilder()
                // migration goes here
                .build();

        journeyRepository.save(updatedEntity);
    }


}
