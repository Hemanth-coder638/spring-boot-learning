package com.hemanth.DependencyInjectionDemo2;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ChoclateSyrupType implements SyrupType{
    public String syrupType(){
        return "Chocolate syrup";
    }
}
