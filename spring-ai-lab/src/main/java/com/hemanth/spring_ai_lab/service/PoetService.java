package com.hemanth.spring_ai_lab.service;

import com.hemanth.spring_ai_lab.model.PoemResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class PoetService {

    private final ChatClient chatClient;

    public PoetService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public PoemResponse generatePoem(String topic, String language) {

        String prompt = """
                You are a sarcastic poet.
                Write a sarcastic poem.

                Topic: %s
                Language: %s

                Return response in JSON with:
                title
                poem_text
                rhyme_scheme
                """.formatted(topic, language);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(PoemResponse.class);
    }
}