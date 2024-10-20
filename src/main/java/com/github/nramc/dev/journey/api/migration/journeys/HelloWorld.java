package com.github.nramc.dev.journey.api.migration.journeys;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class HelloWorld implements Runnable {
    private final JourneyRepository journeyRepository;

    public HelloWorld(JourneyRepository journeyRepository) {
        this.journeyRepository = journeyRepository;
    }

    @Override
    public void run() {
        List<JourneyEntity> entities = journeyRepository.findAll().stream().filter(journey -> journey.getName().startsWith("Marriage "))
                .map(journey -> journey.toBuilder().name("Marriage Alaparaigal").build()).toList();
        journeyRepository.saveAll(entities);
        log.info("No of journeys exists: {}", entities.size());
    }
}
