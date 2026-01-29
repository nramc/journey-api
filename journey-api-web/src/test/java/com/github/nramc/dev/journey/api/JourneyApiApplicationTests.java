package com.github.nramc.dev.journey.api;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import com.github.nramc.dev.journey.api.gateway.cloudinary.CloudinaryGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestContainersConfiguration.class})
@ActiveProfiles("test")
@AutoConfigureRestTestClient
@Testcontainers
class JourneyApiApplicationTests {
    @Container
    static GenericContainer<?> mailpitContainer = new GenericContainer<>(DockerImageName.parse("axllent/mailpit:latest"))
            .withExposedPorts(1025, 8025)
            .waitingFor(Wait.forLogMessage(".*accessible via.*", 1));

    @DynamicPropertySource
    static void configureMail(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", mailpitContainer::getHost);
        registry.add("spring.mail.port", mailpitContainer::getFirstMappedPort);
        registry.add("mailpit.web.port", () -> mailpitContainer.getMappedPort(8025));
    }

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RestTestClient restTestClient;
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
                restTestClient.get().uri("/actuator/health").exchange()
                        .expectStatus().isOk());
    }

    @Test
    void prometheusEndpoint() {
        assertDoesNotThrow(() ->
                restTestClient.get().uri("/actuator/prometheus").exchange()
                        .expectStatus().isOk());
    }

}
