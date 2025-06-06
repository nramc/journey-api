package com.github.nramc.dev.journey.testing.integration;

import com.github.nramc.dev.journey.testing.integration.extension.MyBeforeAllExtension;
import com.github.nramc.dev.journey.testing.integration.extension.MyBeforeEachExtension;
import com.github.nramc.dev.journey.testing.integration.extension.MyBeforeEachMethodExtension;
import com.github.nramc.dev.journey.testing.integration.extension.MyBeforeTestMethodExtension;
import com.github.nramc.dev.journey.testing.integration.support.extension.ExtendWithProfileCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWithProfileCondition(profiles = "dev", extensions = {MyBeforeEachExtension.class, MyBeforeAllExtension.class})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("dev")
class HelloWorldProfileConditionITCase {
    @Autowired
    Environment environment;

    @Test
    @ExtendWithProfileCondition(profiles = {"prod", "dev"}, extensions = {MyBeforeEachMethodExtension.class})
    void myHelloWorld_1() {
        System.out.println("myHelloWorld_1 > Hello, World!");
        assertThat(environment.matchesProfiles("dev")).isTrue();
    }

    @Test
    @ExtendWithProfileCondition(profiles = "dev", extensions = {MyBeforeTestMethodExtension.class})
    void myHelloWorld_2() {
        System.out.println("myHelloWorld_2 > Hello, World!");
        assertThat(environment.matchesProfiles("dev")).isTrue();
    }
}
