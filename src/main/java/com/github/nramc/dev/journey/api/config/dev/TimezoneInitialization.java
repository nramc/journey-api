package com.github.nramc.dev.journey.api.config.dev;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.TimeZone;

@Configuration
@Slf4j
public class TimezoneInitialization {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        log.info("Timezone initialized. now:[{}]", LocalDateTime.now());
    }
}
