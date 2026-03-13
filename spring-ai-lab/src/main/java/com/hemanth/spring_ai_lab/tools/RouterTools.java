package com.hemanth.spring_ai_lab.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class RouterTools {

    @Tool(
            name = "rebootRouter",
            description = "Restart the user's router when internet is not working"
    )
    public String rebootRouter(String serialNo) {

        System.out.println("Router reboot requested for serial: " + serialNo);

        return "Router with serial number " + serialNo + " has been restarted successfully.";
    }
}
