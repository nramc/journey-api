package com.github.nramc.dev.journey.api.infrastructure.openapi;

import com.github.nramc.dev.journey.api.infrastructure.config.TestContainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void openapiEndpoint_shouldReturnValidDocument() throws Exception {
        mockMvc.perform(get("/doc/openapi"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").value("3.0.1"))
                .andExpect(jsonPath("$.info.title").value("Journey API"))
                .andExpect(jsonPath("$.info.version").isNotEmpty())
                .andExpect(jsonPath("$.info.description").isNotEmpty())
                .andExpect(jsonPath("$.servers").isArray())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.basicScheme.scheme").value("basic"));
    }

    @Test
    void openapiEndpoint_shouldContainCorePaths() throws Exception {
        mockMvc.perform(get("/doc/openapi"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/rest/journey']").exists())
                .andExpect(jsonPath("$.paths['/rest/login']").exists())
                .andExpect(jsonPath("$.paths['/rest/ai/enhance-narration']").exists())
                .andExpect(jsonPath("$.paths['/api/tts/synthesize']").exists());
    }
}
