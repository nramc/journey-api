package com.github.nramc.dev.journey.api;

import com.github.nramc.dev.journey.api.migration.journeys.ImportJourneyMigrationRule;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tools.jackson.databind.ObjectMapper;

@SpringBootApplication
@Slf4j
public class JourneyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JourneyApiApplication.class, args);
    }


    CommandLineRunner commandLineRunner(JourneyRepository journeyRepository, ObjectMapper objectMapper) {
        return args -> {
            log.info("######## Migration support started ########");
            ImportJourneyMigrationRule migrationRule = new ImportJourneyMigrationRule(journeyRepository, objectMapper);
            migrationRule.run();
            log.info("######## Migration support completed ########");
        };
    }

}
