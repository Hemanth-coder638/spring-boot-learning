package com.hemanth.spring_ai_lab.controller;

import com.hemanth.spring_ai_lab.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SongController {

    private final SongService vibeService;


    @GetMapping("/match-vibe")
    public List<Document> matchVibe(@RequestParam String feeling) {
        return vibeService.searchVibe(feeling);
    }
}