package com.github.nramc.dev.journey.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JourneyApiApplicationTests {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(applicationContext);
    }

    @Test
    void testHealthCheckEndpoint() {
        webTestClient.get().uri("/actuator/health").exchange()
                .expectStatus().isOk();
    }

}
