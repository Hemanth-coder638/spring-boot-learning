package com.hemanth.spring_ai_lab.controller;

import com.hemanth.spring_ai_lab.service.StockAgentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock-agent")
public class StockController {

    private final StockAgentService service;

    public StockController(StockAgentService service) {
        this.service = service;
    }

    @GetMapping
    public String askAgent(@RequestParam String message) {
        return service.askAgent(message);
    }
}
