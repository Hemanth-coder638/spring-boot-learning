package com.hemanth.spring_ai_lab.controller;

import com.hemanth.spring_ai_lab.service.RouterAgentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouterController {

    private final RouterAgentService routerAgentService;

    public RouterController(RouterAgentService routerAgentService) {
        this.routerAgentService = routerAgentService;
    }

    @GetMapping("/support")
    public String ask(@RequestParam String question) {
        return routerAgentService.askQuestion(question);
    }
}