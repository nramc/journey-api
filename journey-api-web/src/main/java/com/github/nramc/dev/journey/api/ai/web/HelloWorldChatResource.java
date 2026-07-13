package com.github.nramc.dev.journey.api.ai.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "AI Chat", description = "Simple pass-through chat prompts to the configured AI model")
public class HelloWorldChatResource {
    private final ChatClient chatClient;

    public HelloWorldChatResource(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Operation(summary = "Send a prompt to the AI model", description = "Returns the raw text response from the AI chat client.")
    @ApiResponse(responseCode = "200", description = "AI response text",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions")

    @GetMapping("/rest/ai/hello")
    public String helloWorld(
            @Parameter(description = "Prompt text to send to the AI model", example = "Tell me a fun fact about Munich")
            @RequestParam("prompt") String prompt) {
        return this.chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

}
