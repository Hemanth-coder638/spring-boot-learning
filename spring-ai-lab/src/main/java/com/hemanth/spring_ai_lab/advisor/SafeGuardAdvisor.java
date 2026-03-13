package com.hemanth.spring_ai_lab.advisor;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class SafeGuardAdvisor implements Advisor, Ordered {

    @Override
    public String getName() {
        return "safe-guard-advisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

}