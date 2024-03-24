package com.github.nramc.dev.journey.api.web.resources.rest.create;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.nramc.dev.journey.api.web.resources.Resources.CREATE_JOURNEY;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CreateJourneyResource.class})
@ActiveProfiles({"prod", "test"})
class CreateJourneyResourceTest {
    @Autowired
    private MockMvc mockMvc;


    @Test
    void testContext() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    void create_whenJourneyCreatedSuccessfully_shouldReturnCreatedResourceUrl() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_JOURNEY)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

}