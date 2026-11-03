package com.hemanth.learnmultithreading1;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

@Service
public class H4DynamicEmailSchedulerService {
    private final TaskScheduler taskScheduler;
    private final H4EmailService emailService;
    public H4DynamicEmailSchedulerService(TaskScheduler taskScheduler,H4EmailService emailService){
        this.taskScheduler=taskScheduler;
        this.emailService=emailService;
    }
    public ScheduledFuture<?> scheduleEmail(String to,String subject,String body){
        Instant scheduledTime=Instant.now().plusSeconds(10);
        System.out.println("Email scheduled for: "+scheduledTime);
        return taskScheduler.schedule(()->emailService.sendEmail(to,subject,body),scheduledTime);
    }
}
