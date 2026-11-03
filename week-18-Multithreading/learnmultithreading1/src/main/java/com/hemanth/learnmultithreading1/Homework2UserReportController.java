package com.hemanth.learnmultithreading1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class Homework2UserReportController {
    private final Homework2UserReportService userReportService;

    public Homework2UserReportController(Homework2UserReportService userReportService){
        this.userReportService=userReportService;
    }
    @GetMapping("/user-report-async")
    public CompletableFuture<String> getAsyncReport(){
        return userReportService.getUserReport();
    }
    @GetMapping("/user-report-sequential")
    public String getSequentialReport(){
        return userReportService.getUserReportSequentialDemo();
    }
}
