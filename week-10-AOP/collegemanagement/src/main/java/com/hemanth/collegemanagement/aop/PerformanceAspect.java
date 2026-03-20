package com.hemanth.collegemanagement.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Aspect
@Component
public class PerformanceAspect {

    // Store execution times
    private static final Map<String, Long> executionReport = new ConcurrentHashMap<>();

    @Around("@within(com.hemanth.collegemanagement.aop.annotation.TrackExecutionTime)||"+"@annotation(com.hemanth.collegemanagement.aop.annotation.TrackExecutionTime)")
    public Object trackTime(ProceedingJoinPoint joinPoint) throws Throwable {

        String methodName = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed(); // execute method
        long end = System.currentTimeMillis();
        long timeTaken = end - start;

        // Store in report
        executionReport.put(methodName, timeTaken);

        System.out.println("Method: " + methodName + " took " + timeTaken + " ms");
        printReport();
        return result;
    }

    // Optional: Method to print full report
    public static void printReport() {
        System.out.println("\n========= PERFORMANCE REPORT =========");

        executionReport.forEach((method, time) -> {
            System.out.println(method + " -> " + time + " ms");
        });

        System.out.println("======================================\n");
    }
}