# Week 13 — Microservices Architecture Part 2

This week focused on building production-style microservices features such as Gateway filters, centralized configuration, refreshable config, distributed tracing, and centralized logging.

---

## Quick Definitions

**API Gateway Filter** — A request interceptor used to inspect, modify, block, or forward incoming requests.

**Authentication** — Verifies who the user is by checking a valid token.

**Authorization** — Verifies what the user is allowed to access based on roles.

**Centralized Configuration Server** — A single place to store and manage microservice configuration files.

**Refresh Scope** — Reloads updated configuration without restarting the application.

**Distributed Tracing** — Tracks a request across multiple services using trace and span IDs.

**Zipkin** — A tracing UI used to visualize distributed request flow.

**Micrometer** — A metrics and tracing library that helps send trace data to Zipkin.

**ELK Stack** — Elasticsearch, Logstash, and Kibana used for centralized logging and log visualization.

---

## Course Content

### 13.1 Introduction to API Gateway Filters
API Gateway filters intercept requests before they reach microservices. They are used for logging, validation, authentication, and request modification.

**Syntax Example**
```java
public GatewayFilter apply(Config config) {
    return (exchange, chain) -> chain.filter(exchange);
}
```

---

### 13.2 Authentication in API Gateway using custom GatewayFilters
A custom authentication filter reads the JWT token from the request header, validates it, and extracts user information.

**Syntax Example**
```java
String token = authorizationHeader.substring(7);
Long userId = jwtService.getUserIdFromToken(token);
```

---

### 13.3 Centralized Configuration Server using GitHub
Spring Cloud Config Server stores service configuration files in GitHub and serves them centrally to all microservices.

**Syntax Example**
```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
```

---

### 13.4 Refresh Configuration without Restart
Using Actuator and Refresh Scope, configuration changes can be loaded dynamically without restarting services.

**Syntax Example**
```java
@RefreshScope
@Configuration
public class FeaturesEnableConfig {
}
```

---

### 13.5 Distributed Tracing using Zipkin & Micrometer
Zipkin and Micrometer track request flow across services and show how long each service takes to process the request.

**Syntax Example**
```yaml
management:
  tracing:
    sampling:
      probability: 1.0
```

---

### 13.6 Centralized Logging with the ELK Stack
Logs from all services are forwarded to Logstash, stored in Elasticsearch, and viewed in Kibana.

**Syntax Example**
```xml
<appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
    <destination>localhost:5000</destination>
</appender>
```

---

## Homework Assignments

### Homework 1: API Gateway Authorization with JWT Roles
Intercept incoming requests in API Gateway, read the `X-User-Roles` header, parse the JWT token, and allow only users with the required roles to access downstream services.

**Flow**
```text
Client -> API Gateway -> Authentication Filter -> Authorization Filter -> Microservice
```

**Example**
- Token contains roles: `USER, ADMIN`
- Route allows: `USER, ADMIN, SUPER_ADMIN`
- Request is allowed

**Video Placeholder**

<!-- Add working video here -->

---

### Homework 2: Distributed Tracing with Zipkin and Micrometer
Implement tracing across the eCommerce application so that each request can be tracked from the gateway to the downstream services.

**Flow**
```text
API Gateway -> Order Service -> Inventory Service -> Shipping Service
```

**Example**
- One trace ID should appear in Zipkin for the full request flow
- Each service should show its own span

**Video Placeholder**

<!-- Add working video here -->

---

### Homework 3: Learn ElasticSearch and Kibana Dashboards
Understand how logs are stored in Elasticsearch and visualized in Kibana dashboards.

**Flow**
```text
Spring Boot Apps -> Logstash -> Elasticsearch -> Kibana
```

**Example**
- Search logs by service name
- View errors and request flow in Kibana Discover

**Video Placeholder**

<!-- Add working video here -->

---

### Homework 4: Centralized Configuration Service
Create a Config Server that keeps all microservice configurations in one place. Move common configurations into `application.yml` and service-specific configurations into separate YAML files.

**Example Structure**
```text
application.yml
api-gateway.yml
inventory-service.yml
order-service.yml
shipping-service.yml
```

**Example**
- Common Eureka config goes into `application.yml`
- Gateway routes go into `api-gateway.yml`
- DB config goes into each service file

**Video Placeholder**

<!-- Add working video here -->

---

# ⚠️ Challenges Faced & Solutions (Real-World Debugging)

### 1. Config Server Not Fetching from GitHub
**Issue:** API Gateway failed to load config (`Unable to load config data`).  
**Root Cause:** Incorrect `spring.config.import` syntax or GitHub repo access issues.  
**Fix:**
- Correct syntax:
```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
```
- Ensure repo is **public** OR provide correct credentials
- Verify URL in browser:
```
http://localhost:8888/api-gateway/default
```

---

### 2. Config Server Running but No Response in Browser
**Issue:** `localhost:8888` showing blank page  
**Explanation:** Config Server does NOT have UI  
**Fix:** Use correct endpoint format:
```
http://localhost:8888/{application-name}/{profile}
```
Example:
```
http://localhost:8888/api-gateway/default
```

---

### 3. Gateway Not Reading allowedRoles Properly
**Issue:** `allowedRoles` showing incorrect values (`-USER`)  
**Root Cause:** YAML indentation issue  
**Fix:**
```yaml
allowedRoles:
  - USER
  - ADMIN
  - SUPER_ADMIN
```

---

### 4. Authorization Always Failing (403)
**Issue:** Roles matched but still `FORBIDDEN`  
**Root Cause:** String mismatch due to formatting  
**Fix:** Ensure:
- No extra spaces or symbols
- Proper parsing:
```java
Set<String> userRoles = Arrays.stream(header.split(","))
        .map(String::trim)
        .collect(Collectors.toSet());
```

---

### 5. Zipkin Not Showing Traces
**Issue:** No traces visible in UI  
**Fix:**
- Add dependency (Micrometer Tracing)
- Configure:
```yaml
management:
  tracing:
    sampling:
      probability: 1.0
```
- Ensure Zipkin running on `9411`

---

### 6. ELK Logstash Port 5000 Not Opening
**Issue:** Browser shows nothing on port 5000  
**Explanation:** Logstash is NOT a UI service  
**Fix:**
- Check logs via Docker:
```
docker logs logstash
```
- Verify pipeline is running

---

### 7. ELK Logs Not Appearing in Kibana
**Fix Checklist:**
- Create index pattern: `logstash-*`
- Verify Elasticsearch is running (`9200`)
- Confirm logs are reaching Logstash

---

# 🚀 Key Takeaway

Most failures were due to:
- YAML indentation mistakes
- Incorrect config-server URL formats
- Misunderstanding of tool behavior (no UI for Config Server & Logstash)

This debugging experience reflects **real industry scenarios**, where configuration issues consume more time than coding.


## Project Highlights

- Custom API Gateway filters for authentication and authorization
- Centralized GitHub-based configuration management
- Refreshable configuration without restarting services
- Distributed tracing with Zipkin
- Centralized logging with ELK Stack

---

## Final Outcome

By the end of Week 13, the eCommerce microservices system became more secure, configurable, traceable, and production-ready.

