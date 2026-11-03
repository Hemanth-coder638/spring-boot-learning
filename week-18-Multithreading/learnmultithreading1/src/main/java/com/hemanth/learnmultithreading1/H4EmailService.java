//4.Implement a dynamic task scheduler using Spring’s TaskScheduler that allows the user to call an
//API that adds a task to send an email after 10 seconds.
package com.hemanth.learnmultithreading1;

import org.springframework.stereotype.Service;

@Service
public class H4EmailService {
    public void sendEmail(String to,String subject,String body){
        System.out.println("EMAIL SENDING STARTED");
        System.out.println("To :"+to);
        System.out.println("Subject :"+subject);
        System.out.println("Message :"+body);
        System.out.println("Executed by: "+Thread.currentThread().getName());
        System.out.println("EMAIL SENT SUCCESSFULLY");
    }
}
