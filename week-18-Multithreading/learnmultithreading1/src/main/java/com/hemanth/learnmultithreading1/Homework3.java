//3.Implement a custom rejection policy in a ThreadPoolExecutor that logs a warning message and
//retries rejected tasks after 1 second.
package com.hemanth.learnmultithreading1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Homework3 {
    public void executeHomework3(){
        ThreadPoolExecutor executor=new ThreadPoolExecutor(2,5,30, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10),new RetryRejectedExecutionHandler());
        for(int i=1;i<=20;i++){
            final int taskId =i;
            executor.execute(()->{
                System.out.println("Task "+taskId +"started on "+Thread.currentThread().getName());
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                System.out.println("Task "+ taskId +"completed");
            });
        }
        System.out.println("All 20 tasks submitted");
        executor.shutdown();
        try{
            if(!executor.awaitTermination(1,TimeUnit.MINUTES)){
                executor.shutdown();
            }
        } catch (InterruptedException e) {
            executor.shutdown();
            Thread.currentThread().interrupt();
        }
        System.out.println("All tasks finished");
    }

    static class RetryRejectedExecutionHandler implements RejectedExecutionHandler{
        @Override
        public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
            System.out.println("Warning: Task rejected! Retrying after 1 second...");
            while(!executor.isShutdown()){
                try{
                    Thread.sleep(1000);
                    if(executor.isShutdown()){
                        throw new IllegalStateException("Executor is shutting down");
                    }
                    if(executor.getQueue().offer(task,1,TimeUnit.SECONDS)){
                        System.out.println("Task successfully added to queue after retry");
                        return;
                    }
                    System.out.println("WARNING: Queue still full. Retrying again......");
                }catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Retry interrupted",e);
                }
            }
            throw new IllegalStateException("Executor shut down before task could be retried");
        }
    }
}

