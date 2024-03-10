package com.github.nramc.dev.journey.api;

import com.github.nramc.dev.journey.api.data.entity.LocationEntity;
import com.github.nramc.dev.journey.api.data.repository.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class JourneyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JourneyApiApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(LocationRepository locationRepository) {
        return args -> {
            LocationEntity entity = new LocationEntity("Munich");
            locationRepository.save(entity);

            log.info("Number of documents found: {}", locationRepository.count());

            locationRepository.findAll().forEach(location -> log.info("Found: {}", location));
        };
    }

}
