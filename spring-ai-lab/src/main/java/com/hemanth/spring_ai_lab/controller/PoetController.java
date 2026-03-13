package com.hemanth.spring_ai_lab.controller;

import com.hemanth.spring_ai_lab.model.PoemResponse;
import com.hemanth.spring_ai_lab.service.PoetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PoetController {

    private final PoetService poetService;

    public PoetController(PoetService poetService) {
        this.poetService = poetService;
    }

    @GetMapping("/poem")
    public PoemResponse generatePoem(@RequestParam("topic") String topic, @RequestParam("lang") String language) {
        return poetService.generatePoem(topic, language);

    }
}