package com.github.nramc.dev.journey.api;

import com.github.nramc.dev.journey.api.migration.journeys.HelloWorld;
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
            HelloWorld helloWorld = new HelloWorld(journeyRepository);
            helloWorld.run();
            log.info("######## Migration support completed ########");
        };
    }

}
