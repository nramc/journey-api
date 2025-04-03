package com.github.nramc.dev.journey.api.tests.testcase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringJUnitConfig
class HelloWorldTest {
    @Autowired
    Environment environment;

    @Test
    void hellowWorld() {
        assertDoesNotThrow(() -> {
            System.out.println("Hello World! " + Arrays.toString(environment.getActiveProfiles()));
        });
    }
}
