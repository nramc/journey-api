package com.github.nramc.dev.journey.testing.integration;

import com.github.nramc.dev.journey.testing.integration.extension.MyBeforeEachMethodExtension;
import com.github.nramc.dev.journey.testing.integration.support.extension.ExtendWithEnvCondition;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldEnvConditionITCase {

    @Test
    @ExtendWithEnvCondition(variables = {"ENV_TEST_TYPE=SMOOTH", "ENV_TEST_TYPE=ROUGH"}, extensions = {MyBeforeEachMethodExtension.class})
    void myHelloWorld_1() {
        IO.println("myHelloWorld_1 > Hello, World!");
        assertThat(System.getenv()).isNotNull();
    }

    @Test
    @ExtendWithEnvCondition(variables = {"ENV_TEST_TYPE=ROUGH"}, extensions = {MyBeforeEachMethodExtension.class})
    void myHelloWorld_2() {
        IO.println("myHelloWorld_2 > Hello, World!");
        assertThat(System.getenv()).isNotNull();
    }
}
