package com.hemanth.spring_ai_lab.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class StockTools {

    @Tool(description = "Get current stock price for a ticker symbol")
    public double getStockPrice(String ticker) {

        double price = 100 + new Random().nextInt(150);

        System.out.println("Tool called: getStockPrice for " + ticker + " -> " + price);

        return price;
    }

    @Tool(description = "Buy stock shares")
    public String buyStock(String ticker, int quantity) {

        System.out.println("Tool called: buyStock for " + ticker + " qty " + quantity);

        return "Bought " + quantity + " shares of " + ticker;
    }
}
