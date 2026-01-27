package com.github.nramc.dev.journey.api.web.resources.rest.ai.narration;

import com.github.nramc.dev.journey.api.config.security.WebSecurityConfig;
import com.github.nramc.dev.journey.api.config.security.WithMockAuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NarrationEnhancerResource.class)
@Import({WebSecurityConfig.class})
@ActiveProfiles({"test"})
class NarrationEnhancerResourceTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        ChatClient.Builder mockChatClientBuilder() {
            ChatClient.Builder builder = Mockito.mock(ChatClient.Builder.class);
            ChatClient chatClient = Mockito.mock(ChatClient.class);
            when(builder.build()).thenReturn(chatClient);
            return builder;
        }

    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ChatClient.Builder mockChatClientBuilder;
    ChatClient.ChatClientRequestSpec chatRequestSpec = Mockito.mock(ChatClient.ChatClientRequestSpec.class);
    ChatClient.CallResponseSpec chatResponseSpec = Mockito.mock(ChatClient.CallResponseSpec.class);

    @BeforeEach
    void setup() {
        ChatClient chatClient = mockChatClientBuilder.build();

        when(chatClient.prompt()).thenReturn(chatRequestSpec);
        when(chatRequestSpec.system(Mockito.anyString())).thenReturn(chatRequestSpec);
        when(chatRequestSpec.user(Mockito.anyString())).thenReturn(chatRequestSpec);
        when(chatRequestSpec.call()).thenReturn(chatResponseSpec);
    }

    @Test
    @WithMockAuthenticatedUser
    void enhanceNarration_withValidRequest_returnsEnhancedNarration() throws Exception {
        String expectedResponse = "The walk through Tiergarten was a peaceful journey through nature's heart in the middle of Berlin.";
        when(chatResponseSpec.content()).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/rest/ai/enhance-narration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "tone": "warm and inspiring",
                                      "narration": "Visited Berlin and walked through Tiergarten."
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.narration").value(expectedResponse))
                .andExpect(jsonPath("$.tone").value("warm and inspiring"));
    }

}
