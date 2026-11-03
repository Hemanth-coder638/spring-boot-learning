package com.hemanth.learnmultithreading1;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;

@Service
public class Homework2UserReportService {
    private final Homework2DataFetchService dataFetchService;
     public Homework2UserReportService(Homework2DataFetchService dataFetchService){
         this.dataFetchService=dataFetchService;
     }

     public CompletableFuture<String> getUserReport(){
         LocalTime beforeThread=LocalTime.now();
         System.out.println("Starting user report with Async at time= "+beforeThread);
         CompletableFuture<String> userFuture= dataFetchService.fetchUserData();
         CompletableFuture<String> orderFuture= dataFetchService.fetchOrderData();
         CompletableFuture<String> paymentFuture= dataFetchService.fetchPaymentData();
         return CompletableFuture.allOf(userFuture,orderFuture,paymentFuture)
                 .thenApply(ignored->{
                     String user= userFuture.join();
                     String orders= orderFuture.join();
                     String payments=paymentFuture.join();
                     LocalTime afterThread=LocalTime.now();
                     System.out.println("Ending user report with Async at time= "+afterThread);
                     return """
                    USER REPORT STARTING WITH ASYNC AT TIME= %s<br>
                    %s<br>
                    %s<br>
                    %s<br>
                    USER REPORT ENDED WITH ASYNC AT TIME= %s
                    """.formatted(beforeThread,user, orders, payments,afterThread);
                 });
     }
    public String getUserReportSequentialDemo(){
        LocalTime beforeThread=LocalTime.now();
        System.out.println("Starting user report with sequential demo at time= "+beforeThread);

        String user=fetchSequentially("User details: Hemanth",2000);
        String orders=fetchSequentially("Order details: Laptop and Keyboard",3000);
        String payments=fetchSequentially("Payment history:$25000",4000);

        LocalTime AfterThread=LocalTime.now();
        System.out.println("Ending user report sequential demo at time= "+AfterThread);
        return """
                   USER REPORT STARTING WITH SEQUENTIAL AT TIME= %s<br>
                    %s<br>
                    %s<br>
                    %s<br>
                   USER REPORT ENDING WITH SEQUENTIAL AT TIME= %s
                   """.formatted(beforeThread,user, orders, payments,AfterThread);
    }
    private String fetchSequentially(String result,long delay){
         try{
             Thread.sleep(delay);
         }catch(InterruptedException e){
             Thread.currentThread().interrupt();
             throw new IllegalStateException("Fetch interrupted",e);
         }
         return result;
    }
}
