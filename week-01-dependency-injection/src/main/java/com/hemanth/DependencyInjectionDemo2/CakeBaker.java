package com.hemanth.DependencyInjectionDemo2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CakeBaker {
    private final FrostingType frostingType;
    private final SyrupType syrupType;
    @Autowired
    public CakeBaker(FrostingType frostingType,SyrupType syrupType){
        this.frostingType=frostingType;
        this.syrupType=syrupType;
    }
    public void bakeCake(){
        System.out.println("Your cake with "+frostingType.frostingType()+" and "+syrupType.syrupType()+" is ready.....");
    }
}
