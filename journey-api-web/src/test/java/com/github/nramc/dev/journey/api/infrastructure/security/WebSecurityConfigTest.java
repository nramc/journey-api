package com.github.nramc.dev.journey.api.infrastructure.security;

import com.github.nramc.dev.journey.api.infrastructure.actuator.ApplicationProperties;
import com.github.nramc.dev.journey.api.infrastructure.web.mvc.home.HomeResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeResource.class)
@Import({WebSecurityConfig.class})
@ActiveProfiles({"prod", "test"})
@EnableConfigurationProperties({ApplicationProperties.class})
class WebSecurityConfigTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void preflightRequest_shouldBePermitted() throws Exception {
        mvc.perform(MockMvcRequestBuilders.options("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
