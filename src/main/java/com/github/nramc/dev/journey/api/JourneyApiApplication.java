package com.github.nramc.dev.journey.api;

import com.github.nramc.dev.journey.api.migration.journeys.JourneyEntityMigrationRule;
import com.github.nramc.dev.journey.api.repository.journey.JourneyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class JourneyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JourneyApiApplication.class, args);
    }


    CommandLineRunner commandLineRunner(JourneyRepository journeyRepository) {
        return args -> {
            log.info("######## Migration support started ########");
            JourneyEntityMigrationRule migrationRule = new JourneyEntityMigrationRule(journeyRepository);
            migrationRule.run();
            log.info("######## Migration support completed ########");
        };
    }

}
