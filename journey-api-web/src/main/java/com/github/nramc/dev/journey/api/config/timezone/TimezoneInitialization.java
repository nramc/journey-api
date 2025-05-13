package com.github.nramc.dev.journey.api.config.timezone;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.TimeZone;

@Configuration
@Slf4j
public class TimezoneInitialization {

    /**
     * MongoDB is running with UTC time zone.
     * When Journey API service running platform contains time zone other than UTC,
     * MongoDB Query parameter binding iss not working as expected for date and timestamp values.
     * To fix the timezone difference, either JPA repository layer has to be adopted to handle UTC zone conversion before binding values
     * or service has to be adopted to use UTC format.
     *
     * <p>For time being second solution implemented to run service in UTC zone.</p>
     */
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        log.info("Timezone initialized. now:[{}]", LocalDateTime.now());
    }
}
