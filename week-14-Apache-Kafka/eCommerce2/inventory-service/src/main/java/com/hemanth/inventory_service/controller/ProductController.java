package com.hemanth.inventory_service.controller;

import com.hemanth.inventory_service.dto.OrderRequestDto;
import com.hemanth.inventory_service.dto.ProductDto;
import com.hemanth.inventory_service.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PutMapping("restore-stocks")
    public ResponseEntity<Double> restoreStocks(@RequestBody OrderRequestDto orderRequestDto) {
        Double totalPrice = productService.restoreStocks(orderRequestDto);
        return ResponseEntity.ok(totalPrice);
    }
}
