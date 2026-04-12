package com.hemanth.inventory_service.service;

//import com.hemanth.inventory_service.event.OrderCreatedEvent;
//import com.hemanth.inventory_service.event.OrderCreatedItemEvent;
import com.hemanth.common.event.OrderCreatedEvent;
import com.hemanth.common.event.OrderCreatedItemEvent;
import com.hemanth.inventory_service.repository.ProductRepository;
import com.hemanth.inventory_service.dto.OrderRequestDto;
import com.hemanth.inventory_service.dto.OrderRequestItemDto;
import com.hemanth.inventory_service.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Double reduceStocksFromEvent(OrderCreatedEvent event) {

        Double total_price=0.0;
        for (OrderCreatedItemEvent item : event.getItems()) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock");
            }

            total_price+= product.getPrice()* item.getQuantity();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }
        return total_price;
    }

    @Transactional
    public Double restoreStocks(OrderRequestDto orderRequestDto) {
        log.info("Restoring the stocks");
        Double totalPrice = 0.0;

        for (OrderRequestItemDto orderRequestItemDto : orderRequestDto.getItems()) {
            Long productId = orderRequestItemDto.getProductId();
            Integer quantity = orderRequestItemDto.getQuantity();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

            product.setStock(product.getStock() + quantity);
            productRepository.save(product);

            totalPrice += quantity * product.getPrice();
        }

        return totalPrice;
    }



}
