package com.hemanth.collegemanagement.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.hemanth.collegemanagement.service..*(..))")
    public void logBefore(JoinPoint joinPoint) {

        System.out.println("ENTERING METHOD:----");

        System.out.println("Class: " + joinPoint.getTarget().getClass().getSimpleName());
        System.out.println("Method: " + joinPoint.getSignature().getName());

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            System.out.println("Argument: " + arg);
        }
    }

    @After("execution(* com.hemanth.collegemanagement.service..*(..))")
    public void logAfter(JoinPoint joinPoint) {

        System.out.println("EXITING METHOD:-----"
                + joinPoint.getSignature().getName());
    }
}