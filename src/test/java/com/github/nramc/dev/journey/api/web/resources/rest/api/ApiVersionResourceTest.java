package com.github.nramc.dev.journey.api.web.resources.rest.api;

import com.github.nramc.dev.journey.api.config.ApplicationProperties;
import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.web.resources.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiVersionResource.class)
@Import({WebSecurityConfig.class})
@ActiveProfiles({"prod", "test"})
@EnableConfigurationProperties({ApplicationProperties.class})
class ApiVersionResourceTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void version_shouldReturnApiVersion() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(Resources.API_VERSION))
                .andDo(print())
                .andExpect(status().isOk());
    }

}