package com.github.nramc.dev.journey.api;

import com.github.nramc.dev.journey.api.repository.LocationRepository;
import com.github.nramc.dev.journey.api.repository.entity.LocationEntity;
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


    CommandLineRunner commandLineRunner(LocationRepository locationRepository) {
        return args -> {
            LocationEntity entity = new LocationEntity("Munich");
            locationRepository.save(entity);

            log.info("Number of documents found: {}", locationRepository.count());

            locationRepository.findAll().forEach(location -> log.info("Found: {}", location));
        };
    }

}
