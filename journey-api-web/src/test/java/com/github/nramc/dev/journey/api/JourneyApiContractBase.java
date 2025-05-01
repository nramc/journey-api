package com.github.nramc.dev.journey.api;

import com.github.nramc.dev.journey.api.config.TestContainersConfiguration;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = {JourneyApiApplication.class})
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
// Suppressing the warning for the test class as public visibility is required for contract tests
@SuppressWarnings("java:S5786")
public class JourneyApiContractBase {
    @Autowired
    WebApplicationContext context;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.webAppContextSetup(this.context);
    }
}
