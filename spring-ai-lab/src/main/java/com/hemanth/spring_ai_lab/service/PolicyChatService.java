package com.hemanth.spring_ai_lab.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PolicyChatService {

    private final VectorStore policyVectorStore;
    private final ChatClient chatClient;

    public PolicyChatService(@Qualifier("policyVectorStore") VectorStore policyVectorStore,
                             ChatClient.Builder chatClientBuilder) {

        this.policyVectorStore = policyVectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    public String askQuestion(String question) {

        var documents = policyVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(1)
                        .build()
        );

        String context = documents.stream()
                .map(doc -> doc.getFormattedContent())
                .reduce("", (a, b) -> a + "\n" + b);
        System.out.println("CONTEXT:----"+context);

        return chatClient.prompt()
                .user("""
                        Use the following company policy to answer the question.
                        Policy:
                        %s
                        Question:
                        %s
                        """.formatted(context, question))
                .call()
                .content();
    }
}
