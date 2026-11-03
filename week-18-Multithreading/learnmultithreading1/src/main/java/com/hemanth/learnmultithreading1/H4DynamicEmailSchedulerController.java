package com.hemanth.learnmultithreading1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emails")
public class H4DynamicEmailSchedulerController {
    private final H4DynamicEmailSchedulerService schedulerService;
    public H4DynamicEmailSchedulerController(H4DynamicEmailSchedulerService schedulerService){
        this.schedulerService=schedulerService;
    }
    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleEmail(@RequestBody EmailRequest request){
        schedulerService.scheduleEmail(request.to(),request.subject(),request.body());
        return ResponseEntity.ok("Email scheduled successfully "+"It will be sent after 10 seconds");
    }
    public record EmailRequest(String to,String subject,String body){}
}
