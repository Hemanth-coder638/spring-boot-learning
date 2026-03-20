package com.hemanth.collegemanagement.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GlobalExceptionAspect {

// 1.TO HANDLE EXCEPTION AT PARTICULAR METHOD WE CAN USE CUSTOM ANNOTATION BY MENTIONING POINTCUT LIKE BELOW
//    @AfterThrowing(
//       pointcut = "@annotation(com.hemanth.collegemanagement.aop.annotation.HandleException)",
//       throwing = "ex"
//    )
// 2.TO HANDLE EXCEPTION GLOBALLY WE DONT NEED CUSTOM EXCEPTION WE CAN JUST USE BELOW POINTCUT
    @AfterThrowing(pointcut = "execution(* com.hemanth.collegemanagement.service..*(..))",throwing = "ex")
    public void handleException(JoinPoint joinPoint, Exception ex) {

        System.out.println("Global Exception Caught ");

        System.out.println("Class: " + joinPoint.getTarget().getClass().getSimpleName());
        System.out.println("Method: " + joinPoint.getSignature().getName());

        System.out.println("Exception Type: " + ex.getClass().getSimpleName());
        System.out.println("Message: " + ex.getMessage());
    }
}