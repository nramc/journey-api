package com.github.nramc.dev.journey.api.tests.testcase.application;

import com.github.nramc.dev.journey.api.tests.config.EnvironmentProperties;
import com.github.nramc.dev.journey.api.tests.config.IntegrationTestSuiteConfig;
import com.github.nramc.dev.journey.api.tests.config.QaTestSuiteConfig;
import com.github.nramc.dev.journey.api.tests.support.extension.RestAssuredExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringJUnitConfig(classes = {IntegrationTestSuiteConfig.class, QaTestSuiteConfig.class})
@ExtendWith(RestAssuredExtension.class)
class HealthCheckTest {
    @Autowired
    EnvironmentProperties environmentProperties;

    @Test
    void healthCheck_shouldBeAvailable_andShouldBeOK() {
        given()
                .baseUri(environmentProperties.baseUrl())
                .get("/actuator/health")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo("UP"));
    }

}
