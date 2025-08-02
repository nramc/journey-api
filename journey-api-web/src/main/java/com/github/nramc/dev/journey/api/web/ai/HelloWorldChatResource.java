package com.github.nramc.dev.journey.api.web.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldChatResource {
    private final ChatClient chatClient;

    public HelloWorldChatResource(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/rest/ai/hello")
    public String helloWorld(@RequestParam("prompt") String prompt) {
        return this.chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

}
