package com.github.nramc.dev.journey.testing.integration;

import com.github.nramc.dev.journey.testing.integration.extension.MyBeforeEachMethodExtension;
import com.github.nramc.dev.journey.testing.integration.extension.MyBeforeTestMethodExtension;
import com.github.nramc.dev.journey.testing.integration.support.extension.ExtendWithFeatureFlagCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("dev")
@TestPropertySource(properties = {"feature.hello-world.enabled=true", "feature.hello-world-2.enabled=false"})
class HelloWorldFeatureFlagITCase {
    @Autowired
    Environment environment;

    @Test
    @ExtendWithFeatureFlagCondition(property = {"feature.hello-world.enabled"}, extensions = {MyBeforeEachMethodExtension.class})
    void myHelloWorld_1() {
        System.out.println("myHelloWorld_1 > Hello, World!");
        assertThat(environment.getProperty("feature.hello-world.enabled")).isEqualTo("true");
    }

    @Test
    @ExtendWithFeatureFlagCondition(property = "feature.hello-world-2.enabled", extensions = {MyBeforeTestMethodExtension.class})
    void myHelloWorld_2() {
        System.out.println("myHelloWorld_2 > Hello, World!");
        assertThat(environment.getProperty("feature.hello-world-2.enabled")).isEqualTo("false");
    }
}
