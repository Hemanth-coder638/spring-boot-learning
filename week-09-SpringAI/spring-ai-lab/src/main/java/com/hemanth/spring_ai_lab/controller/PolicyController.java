package com.hemanth.spring_ai_lab.controller;

import com.hemanth.spring_ai_lab.service.PolicyChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PolicyController {

    private final PolicyChatService policyChatService;

    public PolicyController(PolicyChatService policyChatService) {
        this.policyChatService = policyChatService;
    }

    @GetMapping("/ask")
    public String askPolicy(@RequestParam String question) {
        return policyChatService.askQuestion(question);
    }
}