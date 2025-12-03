package com.hemanth.DependencyInjectionDemo2;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class StrawberryFrostingType implements FrostingType{
    public String frostingType(){
        return "Strawberry Curve shaped";
    }
}
