# week 12 eCommerce Microservices Project

A hands-on Spring Boot microservices project built to understand the core building blocks of a production-style distributed system: service decomposition, Eureka service discovery, API Gateway routing, OpenFeign communication, and resilience patterns using Resilience4J.

---

## 12.1 Introduction to Microservice Architecture

### Definition
Microservice architecture is a software design style in which an application is split into small, independent services. Each service owns a specific business capability and can be developed, deployed, scaled, and maintained independently.

### Key idea
Instead of building one large monolithic application, the system is divided into focused services such as:
- Order Service
- Inventory Service
- Shipping Service
- API Gateway
- Discovery Service

### Why it is useful
- Easier to scale individual services
- Faster development by different teams
- Fault isolation: one service failure does not necessarily break the whole system
- Better maintainability for large applications

### Basic syntax / structure
There is no Java syntax for microservices as a concept, but the typical structure looks like this:

```text
client -> api-gateway -> order-service -> inventory-service / shipping-service
                          \-> discovery-service (Eureka)
```

### Example
In this project:
- `order-service` handles order creation and order status updates
- `inventory-service` manages stock reduction and restoration
- `shipping-service` checks eligible orders and updates shipping status
- `api-gateway` routes requests to the correct service
- `discovery-service` registers and discovers services dynamically

### Interview-friendly explanation
Microservices are independently deployable business services that communicate over the network. They improve scalability and modularity, but they also introduce complexity in communication, resilience, monitoring, and deployment.

---

## 12.2 Setting Up the Inventory Management System

### Definition
An inventory management system tracks product stock, stock reduction, and stock restoration when order lifecycle events happen.

### Role in this project
The `inventory-service` is responsible for:
- storing products
- maintaining stock count
- reducing stock when an order is confirmed
- restoring stock when an order is cancelled

### Main classes used
- `Product` entity
- `ProductRepository`
- `ProductService`
- `ProductController`
- `InventoryOpenFeignClient` in order-service

### Syntax pattern used
```java
@Transactional
public Double reduceStocks(OrderRequestDto orderRequestDto) {
    for (OrderRequestItemDto item : orderRequestDto.getItems()) {
        Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < item.getQuantity()) {
            throw new RuntimeException("Product cannot be fulfilled for given quantity");
        }

        product.setStock(product.getStock() - item.getQuantity());
        productRepository.save(product);
    }
    return totalPrice;
}
```

### Example
If a product has stock `50` and an order requests `5`, the stock becomes `45` after confirmation.

When the order is cancelled before shipping, stock is restored back by `5`.

### Interview-friendly explanation
Inventory management ensures stock consistency across order lifecycle events. In a microservices system, this logic should stay inside inventory service so that stock operations are centrally controlled and not duplicated.

---

## 12.3 Service Registration and Service Discovery with Eureka

### Definition
Service registration means every microservice registers itself with a central registry. Service discovery means other services can find it dynamically without hardcoding IP addresses or ports.

### Eureka in this project
- `discovery-service` acts as the Eureka Server
- `order-service`, `inventory-service`, `shipping-service`, and `api-gateway` act as Eureka Clients

### Syntax
#### Eureka Server
```java
@EnableEurekaServer
@SpringBootApplication
public class DiscoveryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
}
```

#### Eureka Client properties
```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.instance.prefer-ip-address=true
eureka.instance.instance-id=${spring.application.name}:${server.port}
```

### Example
When `order-service` starts on port `9020`, Eureka registers it dynamically. Then `api-gateway` or `shipping-service` can locate `order-service` by service name instead of hardcoded host and port.

### Interview-friendly explanation
Eureka removes the need to hardcode service locations. It gives dynamic service lookup, which is essential in distributed systems where services can scale up, restart, or move across machines.

---

## 12.4 Spring Cloud API Gateway

### Definition
API Gateway is the single entry point for client requests in a microservice system. It routes incoming requests to the appropriate internal service.

### Why it is used
- hides internal service structure from client
- centralizes routing logic
- provides a common layer for authentication, logging, rate limiting, and monitoring
- reduces direct client-to-service coupling

### Syntax
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/v1/orders/**
```

### Example
A request to:
```text
/api/v1/orders/core/create-order
```
may be routed by API Gateway to:
```text
order-service
```

### Interview-friendly explanation
API Gateway is a reverse proxy that acts as a single front door for all microservices. It simplifies client communication and allows cross-cutting concerns to be handled in one place.

---

## 12.5 OpenFeign Microservice Communication

### Definition
OpenFeign is a declarative HTTP client used to call one microservice from another using Java interfaces instead of manually writing REST calls.

### Why it is useful
- reduces boilerplate code
- keeps service-to-service communication clean
- integrates well with Eureka and Spring Cloud LoadBalancer

### Syntax
```java
@FeignClient(name = "inventory-service", path = "/inventory")
public interface InventoryOpenFeignClient {

    @PutMapping("/products/reduce-stocks")
    Double reduceStocks(@RequestBody OrderRequestDto orderRequestDto);
}
```

### Example
In this project:
- `order-service` uses `InventoryOpenFeignClient` to reduce stock in `inventory-service`
- `shipping-service` can use Feign to talk to `order-service` if needed

### Interview-friendly explanation
OpenFeign makes synchronous inter-service calls simple and readable. It improves development speed and integrates naturally with service discovery.

---

## 12.6 Circuit Breaker, Retry and Rate Limiter with Resilience4J

### Definition
Resilience4J is a fault-tolerance library used to make microservices resilient against failures such as service downtime, slowness, and repeated request overload.

### Core concepts

#### Circuit Breaker
A circuit breaker stops repeated calls to a failing service temporarily. It protects the system from cascading failures.

#### Retry
Retry attempts a failed call again for a fixed number of times before failing completely.

#### Rate Limiter
Rate limiting restricts how many requests can be processed in a specific time window.

### Syntax
```java
@Retry(name = "shippingRetry", fallbackMethod = "fallback")
@CircuitBreaker(name = "shippingCB", fallbackMethod = "fallback")
public String checkShippingService() {
    return shippingClient.pingShipping();
}
```

### Example
In this project:
- `inventoryCircuitBreaker` protects the inventory call inside `order-service`
- `shippingCB` and `shippingRetry` protect shipping-related calls
- fallback methods return safe responses when dependencies fail

### Interview-friendly explanation
Resilience4J improves system reliability by handling transient failures gracefully. Retry helps with temporary issues, circuit breaker prevents repeated failing calls, and rate limiter protects services from overuse.

---

## Project Overview

This repository contains a Spring Boot eCommerce microservices system built as part of the microservices learning journey.

### Services
- `discovery-service` — Eureka server
- `api-gateway` — central routing layer
- `inventory-service` — product and stock management
- `order-service` — order creation, cancellation, and shipping lifecycle
- `shipping-service` — background shipping worker

### Main business flow
1. User places an order through `order-service`
2. Inventory stock is reduced
3. Order becomes `CONFIRMED`
4. Shipping eligibility time is stored in the order
5. `shipping-service` checks eligible orders every 15 seconds
6. When the shipping window is reached, status becomes `SHIPPED`
7. Order can be cancelled only before the shipping window closes

### Architecture snapshot
```text
Client -> API Gateway -> Order Service -> Inventory Service
                                  \-> Shipping Service
                                  \-> Eureka Discovery Service
```

---

## Homework 1: Break Down MakeMyTrip Backend into Microservices

### Requirement
Break down the MakeMyTrip backend into smaller microservices, define responsibilities of each service, and design the interaction flow.

### Microservices Identified
- **User Service** — handles user registration, login, profiles
- **Search Service** — handles flight, hotel, and bus search queries
- **Booking Service** — manages booking creation and lifecycle
- **Payment Service** — handles transactions and payment status
- **Inventory Service** — manages seat/room availability
- **Notification Service** — sends email/SMS confirmations
- **API Gateway** — single entry point
- **Discovery Service (Eureka)** — service registry

### Interaction Flow (High-Level)
```text
Client -> API Gateway -> Booking Service
                       -> Search Service -> Inventory Service
                       -> Payment Service
                       -> Notification Service

All services register with Eureka
```

### Explanation
- User searches → Search Service fetches data from Inventory
- User books → Booking Service creates booking
- Payment → Payment Service processes transaction
- Confirmation → Notification Service sends updates

### Result
A scalable, modular system where each service handles a single responsibility.

### Screenshot placeholder

https://drive.google.com/file/d/1TBpsuZ9IkLcApQQOKWPcwHAeYr3R5s-T/view?usp=drive_link

---

## Homework 2: Cancel Order by Restocking Items

### Requirement
Extend Inventory Service to support order cancellation by restoring stock.

### Implementation summary
- Added `restoreStocks()` method in Inventory Service
- Created `/restore-stocks` endpoint
- Used OpenFeign in Order Service to call inventory
- Updated Order Service cancel logic to:
  - change status to `CANCELLED`
  - restore stock

### Key Logic
```java
public void restoreStocks(OrderRequestDto dto) {
    for (OrderRequestItemDto item : dto.getItems()) {
        Product product = productRepository.findById(item.getProductId()).get();
        product.setStock(product.getStock() + item.getQuantity());
        productRepository.save(product);
    }
}
```

### Result
Stock consistency is maintained even after order cancellation.

### Cancel Order Working Video

https://github.com/user-attachments/assets/a4be81b4-06a9-4666-8708-2735101e040a

---

## Homework 3: Shipping Service Microservice

### Requirement
Create a new ShippingService, register it with Eureka, and integrate it with OrderService.

### Implementation summary
- Created `shipping-service` microservice
- Registered with Eureka
- Added scheduled job (every 15 seconds)
- Fetches eligible orders from Order Service
- Updates order status to `SHIPPED`
- Added Feign client in Order Service for communication

### Key Logic
```java
@Scheduled(fixedRate = 15000)
public void processShipping() {
    List<OrderDto> orders = orderClient.getOrdersEligibleForShipping();
    for (OrderDto order : orders) {
        orderClient.markAsShipped(order.getId());
    }
}
```

### Result
Shipping is handled asynchronously and independently.

### Automatic Shipping Orders working video

https://github.com/user-attachments/assets/e4a43013-3b55-48e2-9efe-8c632daac19a

---

## Homework 4: Circuit Breaker and Retry with Resilience4J

### Requirement
Add fault tolerance when Order Service calls Shipping Service.

### Implementation summary
- Added OpenFeign client for Shipping Service
- Implemented `ShippingCheckService`
- Applied:
  - `@Retry`
  - `@CircuitBreaker`
- Added fallback method

### Key Logic
```java
@Retry(name = "shippingRetry", fallbackMethod = "fallback")
@CircuitBreaker(name = "shippingCB", fallbackMethod = "fallback")
public String checkShippingService() {
    return shippingClient.pingShipping();
}
```

### Fallback
```java
public String fallback(Throwable t) {
    return "Shipping unavailable";
}
```

### Result
- Temporary failures → handled by retry
- Continuous failures → circuit breaker opens
- System remains stable with fallback response

### Circuit Breaker Working video

https://github.com/user-attachments/assets/57660b44-d588-42e0-ae7a-d47cce9a98b4

---

## Important API Endpoints

### Order Service
- `POST /orders/core/create-order`
- `GET /orders/core`
- `GET /orders/core/{id}`
- `PUT /orders/core/{id}/cancel`
- `GET /orders/core/shipping-due`
- `PUT /orders/core/{id}/ship`

### Inventory Service
- `GET /inventory/products`
- `GET /inventory/products/{id}`
- `PUT /inventory/products/reduce-stocks`
- `PUT /inventory/products/restore-stocks`

### Shipping Service
- `GET /test-shipping`
- scheduled background job for shipping checks every 15 seconds

---

## Technologies Used

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Spring Cloud Eureka
- Spring Cloud Gateway
- OpenFeign
- Resilience4J
- Lombok
- ModelMapper

---

## Learning Outcome

This project helped me understand:
- microservice decomposition
- service discovery with Eureka
- gateway-based routing
- inter-service communication using OpenFeign
- resilience patterns with Retry and Circuit Breaker
- asynchronous shipping behavior using scheduler-based polling
- stock restoration during order cancellation

---

## Notes

- Screenshots can be placed inside the `screenshots/` folder.
- Keep filenames matching the placeholders used above.
- This README is written for GitHub submission and interview discussion.

---

## Conclusion

This project is a strong practical demonstration of Spring Boot microservices architecture. It combines real-world ideas such as service registration, gateway routing, stock management, delayed shipping, cancellation rules, and failure handling into one clean learning project.

