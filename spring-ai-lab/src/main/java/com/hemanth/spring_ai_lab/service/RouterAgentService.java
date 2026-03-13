package com.hemanth.spring_ai_lab.service;



import com.hemanth.spring_ai_lab.tools.RouterTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RouterAgentService {

    private final VectorStore routerVectorStore;
    private final ChatClient chatClient;

    public RouterAgentService(@Qualifier("routerVectorStore") VectorStore routerVectorStore,
                              ChatClient.Builder builder,
                              RouterTools routerTools) {

        this.routerVectorStore = routerVectorStore;

        this.chatClient = builder
                .defaultSystem("""
                        You are a router customer support assistant.

                        Use the provided router manual context to answer user questions.

                        If the user asks to restart or reboot the router,
                        call the rebootRouter tool.

                        If the answer exists in the manual, use that information.
                        """)
                .defaultTools(routerTools)
                .build();
    }

    public String askQuestion(String question) {

        var documents = routerVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(2)
                        .build()
        );

        String context = documents.stream()
                .map(doc -> doc.getFormattedContent())
                .reduce("", (a, b) -> a + "\n" + b);

        System.out.println("MANUAL CONTEXT:\n" + context);

        return chatClient.prompt()
                .user("""
                        Router Manual:
                        %s

                        User Question:
                        %s
                        """.formatted(context, question))
                .call()
                .content();
    }
}