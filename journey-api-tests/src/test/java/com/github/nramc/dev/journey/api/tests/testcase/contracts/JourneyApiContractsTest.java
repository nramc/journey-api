package com.github.nramc.dev.journey.api.tests.testcase.contracts;

import com.github.nramc.dev.journey.api.tests.application.JourneyIntegrationApplication;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = JourneyIntegrationApplication.class)
@ActiveProfiles("integration")
@AutoConfigureStubRunner(
        ids = "com.github.nramc.dev.journey:journey-api-web:+:stubs:6565",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@Disabled("Disabled to run CI/CD pipeline, but can be enabled for local testing if needed.")
class JourneyApiContractsTest {

    @Test
    void signup_whenSignupDataValid_shouldReturnSuccess() {
        RestAssured.given()
                .port(6565)
                .contentType("application/json")
                .accept("application/json")
                .body("""
                        {"username":"username@example.com", "password":"Strong@password123", "name":"John Doe"}
                        """)
                .post("/rest/signup")
                .then()
                .statusCode(201);
    }
}
