package com.hemanth.spring_ai_lab.controller;
import com.hemanth.spring_ai_lab.Dto.ChatRequest;
import com.hemanth.spring_ai_lab.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }


    @PostMapping
    public String chat(@RequestBody ChatRequest request) {
        if(request.message().toLowerCase().contains("competitor")){
            return "Request blocked due to restricted word.";
        }
        else {
            return chatService.chat(request.message());
        }
    }
}
