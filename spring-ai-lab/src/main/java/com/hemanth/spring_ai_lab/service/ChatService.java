package com.hemanth.spring_ai_lab.service;

import com.hemanth.spring_ai_lab.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    public ChatService(ChatClient.Builder chatClientBuilder,
                       ChatMemory chatMemory,
                       SafeGuardAdvisor safeGuardAdvisor) {
        this.chatMemory=chatMemory;
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        safeGuardAdvisor,
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    public String chat(String message) {

        System.out.println("------ MEMORY WINDOW BEFORE CALL ------");
        chatMemory.get("default").forEach(m ->
                System.out.println(m.getMessageType() + " : " + m.getText())
        );

        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();

        System.out.println("------ MEMORY WINDOW AFTER CALL ------");
        chatMemory.get("default").forEach(m ->
                System.out.println(m.getMessageType() + " : " + m.getText())
        );

        return response;
    }
}