package com.github.nramc.dev.journey.api;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
class JourneyApiApplicationTests {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void healthCheckEndpoint() {
        assertDoesNotThrow(() -> {
            webTestClient.get().uri("/actuator/health").exchange()
                    .expectStatus().isOk();
        });
    }

}
