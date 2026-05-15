package com.github.nramc.dev.journey.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class JourneyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JourneyApiApplication.class, args);
    }
}
