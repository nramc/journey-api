package com.github.nramc.dev.journey.testing.integration;

import com.github.nramc.dev.journey.testing.integration.extension.MyBeforeEachMethodExtension;
import com.github.nramc.dev.journey.testing.integration.support.extension.ExtendWithEnvironmentVariableCondition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HelloWorldEnvironmentVariableConditionITCase {

    @Test
    @ExtendWithEnvironmentVariableCondition(variables = {"ENV_TEST_TYPE=SMOOTH", "ENV_TEST_TYPE=ROUGH"}, extensions = {MyBeforeEachMethodExtension.class})
    void myHelloWorld_1() {
        System.out.println("myHelloWorld_1 > Hello, World!");
        Assertions.assertTrue(true);
    }

    @Test
    @ExtendWithEnvironmentVariableCondition(variables = {"ENV_TEST_TYPE=ROUGH"}, extensions = {MyBeforeEachMethodExtension.class})
    void myHelloWorld_2() {
        System.out.println("myHelloWorld_2 > Hello, World!");
        Assertions.assertTrue(true);
    }
}
