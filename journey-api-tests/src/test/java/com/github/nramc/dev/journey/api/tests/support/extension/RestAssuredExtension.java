package com.github.nramc.dev.journey.api.tests.support.extension;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * A JUnit 5 extension that configures RestAssured settings for Integration and QA acceptance tests.
 * <p>
 * This extension ensures that RestAssured is properly initialized before all tests in a test class
 * by resetting its configuration, adding common filters for request/response logging and integration with Allure reports.
 * </p>
 *
 * <h2>Key Responsibilities</h2>
 * <ul>
 *     <li>Resets RestAssured's global configuration to ensure a clean state for each test run.</li>
 *     <li>Configures RestAssured filters to:
 *         <ul>
 *             <li>Log request details using {@link RequestLoggingFilter}.</li>
 *             <li>Log response details using {@link ResponseLoggingFilter}.</li>
 *             <li>Attach HTTP requests and responses to Allure reports using {@link AllureRestAssured}.</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * @ExtendWith(RestAssuredExtension.class)
 * public class MyAcceptanceTest {
 *
 *     @Test
 *     void testEndpoint() {
 *         given()
 *             .auth().oauth2("validToken")
 *         .when()
 *             .get("/api/resource")
 *         .then()
 *             .statusCode(200);
 *     }
 * }
 * }</pre>
 *
 * @see RestAssured
 * @see RequestLoggingFilter
 * @see ResponseLoggingFilter
 * @see AllureRestAssured
 * @see BeforeAllCallback
 */
public class RestAssuredExtension implements BeforeAllCallback {
    private static final int TIMEOUT_IN_MILLISECONDS = 10_000;

    @Override
    public void beforeAll(ExtensionContext context) {

        RestAssured.reset();
        RestAssured.filters(
                new AllureRestAssured(),
                new RequestLoggingFilter(),
                new ResponseLoggingFilter()
        );
        RestAssured.config = RestAssured.config().httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", TIMEOUT_IN_MILLISECONDS)
                .setParam("http.socket.timeout", TIMEOUT_IN_MILLISECONDS)
                .setParam("http.connection.max.age", TIMEOUT_IN_MILLISECONDS)
                .setParam("http.socket.max.age", TIMEOUT_IN_MILLISECONDS)
        );
    }
}
