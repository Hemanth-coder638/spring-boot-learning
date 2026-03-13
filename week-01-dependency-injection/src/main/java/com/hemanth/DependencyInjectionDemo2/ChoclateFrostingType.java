package com.hemanth.DependencyInjectionDemo2;

import org.springframework.stereotype.Component;

@Component
public class ChoclateFrostingType implements FrostingType {
    public String frostingType(){
        return "Chocolate Box shaped";
    }

}
