//1. Write a program that uses a ThreadPoolExecutor with the following configuration:
//        • Core pool size: 2
//        • Maximum pool size: 5
//        • Keep-alive time: 30 seconds
//• A bounded queue with a size of 10 Simulate 20 tasks, each taking 2 seconds to complete.
//Observe how threads are utilized and what happens to the excess tasks.
package com.hemanth.learnmultithreading1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Homework1 {
    public void executeHomework1() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
        for (int i = 1; i <= 20; i++) {
            final int taskId = i;
            try {
                executor.execute(() -> {
                    System.out.println("Task " + taskId + " started on " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Task " + taskId + " completed");
                });
            } catch (RejectedExecutionException e) {
                System.out.println("Task " + taskId + "REJECTED");
            }
        }
        System.out.println("Active threads: " + executor.getActiveCount());
        System.out.println("Queued tasks: " + executor.getQueue().size());
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdown();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Termination was interrupted");
        }
        System.out.println("All accepted tasks finished.");
    }
}
