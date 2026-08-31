package com.hemanth.Banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableTransactionManagement
@EnableRetry
public class TransactionConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService transactionExecutor() {
        return Executors.newFixedThreadPool(4);
    }
}