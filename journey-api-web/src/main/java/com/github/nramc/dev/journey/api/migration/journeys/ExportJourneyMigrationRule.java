package com.github.nramc.dev.journey.api.migration.journeys;

import com.github.nramc.dev.journey.api.repository.journey.JourneyEntity;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ExportJourneyMigrationRule implements Runnable {
    private static final String EXPORT_FILE_NAME = "journeys.json";
    private final JourneyRepository journeyRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run() {
        List<JourneyEntity> journeys = journeyRepository.findAll();
        File file = new File(EXPORT_FILE_NAME);
        objectMapper.writeValue(file, journeys);
        log.info("Successfully exported to {} with {} journeys", file.getAbsolutePath(), journeys.size());
    }


}
