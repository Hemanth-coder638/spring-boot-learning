package com.hemanth.spring_ai_lab.service;

import com.hemanth.spring_ai_lab.tools.StockTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class StockAgentService {

    private final ChatClient chatClient;

    public StockAgentService(ChatClient.Builder builder,
                             StockTools stockTools) {

        this.chatClient = builder
                .defaultTools(stockTools)
                .build();
    }

    public String askAgent(String message) {

        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
