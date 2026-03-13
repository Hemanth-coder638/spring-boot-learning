package com.hemanth.DependencyInjectionDemo2;

import org.springframework.stereotype.Component;

@Component
public class StrawberrySyrupType implements SyrupType {
    public String syrupType(){
        return "Strawberry syrup";
    }
}
