package com.github.nramc.dev.journey.api;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
class JourneyApiApplicationTests {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    CloudinaryGateway cloudinaryGateway;

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void healthCheckEndpoint() {
        when(cloudinaryGateway.isAvailable()).thenReturn(true);
        assertDoesNotThrow(() ->
                webTestClient.get().uri("/actuator/health").exchange()
                        .expectStatus().isOk());
    }

    @Test
    void prometheusEndpoint() {
        assertDoesNotThrow(() ->
                webTestClient.get().uri("/actuator/prometheus").exchange()
                        .expectStatus().isOk());
    }

}
