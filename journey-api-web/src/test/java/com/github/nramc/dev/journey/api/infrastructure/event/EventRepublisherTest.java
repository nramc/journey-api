package com.github.nramc.dev.journey.api.infrastructure.event;

import com.github.nramc.dev.journey.api.infrastructure.config.TestContainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest
@ActiveProfiles("test")
@Import({TestContainersConfiguration.class, SimpleEventHandler.class})
class EventRepublisherTest {

    @Autowired
    EventRepublisher republisher;
    @Autowired
    SimpleEventHandler simpleEventHandler;

    @Test
    void contextLoads() {
        assertThat(republisher).isNotNull();
    }

    @Test
    void publish(Scenario scenario) {
        scenario.publish(new SimpleEvent("Hello World!"))
                .andWaitAtMost(Duration.ofMinutes(1))
                .forEventOfType(SimpleEventSuccess.class)
                .toArriveAndVerify(event -> assertThat(event.message()).isEqualTo("Hello World!"));
        assertThat(simpleEventHandler.getInvocationCount()).isEqualTo(3);
    }

}
