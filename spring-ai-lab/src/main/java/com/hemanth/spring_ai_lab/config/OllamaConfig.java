package com.hemanth.spring_ai_lab.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("ollama")
public class OllamaConfig {

    @Bean
    public ChatClient chatClient(OllamaChatModel model) {
        return ChatClient.builder(model).build();
    }
}
