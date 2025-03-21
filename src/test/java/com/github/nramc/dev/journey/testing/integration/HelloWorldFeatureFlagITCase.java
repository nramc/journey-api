package com.github.nramc.dev.journey.testing.integration;

import com.github.nramc.dev.journey.testing.integration.extension.MyBeforeEachMethodExtension;
import com.github.nramc.dev.journey.testing.integration.extension.MyBeforeTestMethodExtension;
import com.github.nramc.dev.journey.testing.integration.support.extension.ExtendWithFeatureFlagCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("dev")
@TestPropertySource(properties = {"feature.hello-world.enabled=true", "feature.hello-world-2.enabled=false"})
class HelloWorldFeatureFlagITCase {

    @Test
    @ExtendWithFeatureFlagCondition(property = {"feature.hello-world.enabled"}, extensions = {MyBeforeEachMethodExtension.class})
    void myHelloWorld_1() {
        System.out.println("myHelloWorld_1 > Hello, World!");
        assertTrue(true);
    }

    @Test
    @ExtendWithFeatureFlagCondition(property = "feature.hello-world-2.enabled", extensions = {MyBeforeTestMethodExtension.class})
    void myHelloWorld_2() {
        System.out.println("myHelloWorld_2 > Hello, World!");
        assertTrue(true);
    }
}
