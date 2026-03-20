package com.hemanth.collegemanagement.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {

    @Around("execution(* com.hemanth.collegemanagement.service..*(..))")
    public Object validateToken(ProceedingJoinPoint joinPoint) throws Throwable {

        Object[] args = joinPoint.getArgs();

        String token = null;

        // Extract token from method arguments
        for (Object arg : args) {
            if (arg instanceof String) {
                token = (String) arg;
                break;
            }
        }

        System.out.println("Checking security token...");

        if (token == null || !token.equals("VALID_TOKEN")) {
            throw new RuntimeException("Invalid or Missing Token");
        }

        // Proceed to actual method
        return joinPoint.proceed();
    }
}