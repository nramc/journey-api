package com.github.nramc.dev.journey.api.tests.testcase;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class HelloWorldTest {

    @Test
    void hellowWorld() {
        assertDoesNotThrow(() -> {
            System.out.println("Hello World!");
        });
    }
}
