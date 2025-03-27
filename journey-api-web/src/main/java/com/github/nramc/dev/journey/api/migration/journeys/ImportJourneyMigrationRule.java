package com.github.nramc.dev.journey.api.migration.journeys;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ImportJourneyMigrationRule implements Runnable {
    private static final String IMPORT_FILE_NAME = "journeys.json";
    private final JourneyRepository journeyRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run() {
        File file = new File(IMPORT_FILE_NAME);
        try {
            List<JourneyEntity> journeys = objectMapper.readValue(file, new TypeReference<>() {
            });
            journeyRepository.saveAll(journeys);
            log.info("Successfully imported to {} journeys from {}", journeys.size(), file.getAbsolutePath());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }


}
