package com.github.nramc.dev.journey.testing.integration;

import com.github.nramc.dev.journey.testing.integration.extension.MyBeforeAllExtension;
import com.github.nramc.dev.journey.testing.integration.extension.MyBeforeEachExtension;
import com.github.nramc.dev.journey.testing.integration.extension.MyBeforeEachMethodExtension;
import com.github.nramc.dev.journey.testing.integration.extension.MyBeforeTestMethodExtension;
import com.github.nramc.dev.journey.testing.integration.support.extension.ExtendWithProfileCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWithProfileCondition(profiles = "dev", extensions = {MyBeforeEachExtension.class, MyBeforeAllExtension.class})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("dev")
class HelloWorldProfileConditionITCase {

    @Test
    @ExtendWithProfileCondition(profiles = {"prod", "dev"}, extensions = {MyBeforeEachMethodExtension.class})
    void myHelloWorld_1() {
        System.out.println("myHelloWorld_1 > Hello, World!");
        assertTrue(true);
    }

    @Test
    @ExtendWithProfileCondition(profiles = "dev", extensions = {MyBeforeTestMethodExtension.class})
    void myHelloWorld_2() {
        System.out.println("myHelloWorld_2 > Hello, World!");
        assertTrue(true);
    }
}
