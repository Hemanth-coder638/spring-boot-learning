//2. Write a Spring Boot application with three @Async methods:
//        • fetchUserData(): Simulate fetching user details (2 seconds delay).
//        • fetchOrderData(): Simulate fetching order details (3 seconds delay).
//        • fetchPaymentData(): Simulate fetching payment history (4 seconds delay). Use these methods
//in a getUserReport() method to fetch all data concurrently and return the combined result.
package com.hemanth.learnmultithreading1;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class Homework2DataFetchService {
    @Async
    public CompletableFuture<String> fetchUserData(){
        simulateDelay(2000);
        String result= "User details: Hemanth";
        System.out.println("User data fetched by "+Thread.currentThread().getName());
        return CompletableFuture.completedFuture(result);
    }
    @Async
    public CompletableFuture<String> fetchOrderData(){
        simulateDelay(3000);
        String result= "Order details: Laptop and Keyboard";
        System.out.println("Order data fetched by "+Thread.currentThread().getName());
        return CompletableFuture.completedFuture(result);
    }
    @Async
    public CompletableFuture<String> fetchPaymentData(){
        simulateDelay(4000);
        String result= "Payment history: $25000";
        System.out.println("Payment data fetched by "+Thread.currentThread().getName());
        return CompletableFuture.completedFuture(result);
    }
    private void simulateDelay(long milliseconds){
        try{
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Data fetching was interrupted",e);
        }
    }
}
