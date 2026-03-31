# Week 11 Homework 1 — Weather Caching Project (Spring Boot + Redis)

## Overview
This project is part of **Week 11** of my Spring Boot learning journey. The goal of this homework was to understand and implement **caching in a real Spring Boot application** using **Redis**. The application fetches weather information for a city from an external weather API, stores the response in Redis, and serves repeated requests much faster from cache.

This project helped me understand not only how caching works in Spring Boot, but also how to configure Redis, control cache expiration using TTL, and avoid unnecessary repeated API calls.

---

## What is Caching?

### Interview-style definition
**Caching is a performance optimization technique where frequently accessed data is stored in a fast temporary storage so that future requests for the same data can be served more quickly without recomputing or re-fetching it from the original source.**

In simple words:
- First request: data comes from the real source, such as an API or database.
- Next request: same data is served from cache.

### Why caching is used
Caching is used to reduce:
- response time
- network calls
- load on external APIs
- load on databases
- cost of repeated processing

---

## Benefits of Caching

Caching gives major performance and architectural advantages:

1. **Faster response time**  
   Cached data is served much faster than calling an external system every time.

2. **Reduced API usage**  
   The project avoids repeated calls to the weather API for the same city.

3. **Lower backend load**  
   Less processing is required from service layers and external services.

4. **Better user experience**  
   Users get quick responses for repeated requests.

5. **Cost savings**  
   Many third-party APIs have request limits. Caching reduces unnecessary usage.

6. **Scalability improvement**  
   Applications with caching can handle more requests efficiently.

---

## Project Idea

The project is a **Weather Caching System**.

### Main flow
1. User sends request for a city.
2. Application checks Redis cache.
3. If data exists in cache, it is returned immediately.
4. If data does not exist, application calls the external weather API.
5. The API response is parsed.
6. Parsed response is stored in Redis.
7. Next request for the same city is served from Redis.

---

## Technologies Used

- **Java**
- **Spring Boot**
- **Spring Cache**
- **Spring Data Redis**
- **Redis Cloud / Local Redis**
- **RestTemplate** or HTTP client for external API call
- **Jackson** for JSON parsing
- **Lombok**

---

## Project Working

### First request
When the city is requested for the first time:
- Redis does not contain the value.
- `@Cacheable` allows the method to execute.
- API call is made.
- API response is converted into `WeatherResponse`.
- The response is stored in Redis.

### Second request
When the same city is requested again:
- Spring checks Redis before executing the method.
- Since data already exists, the method is skipped.
- The stored cached value is returned directly.
- This makes the second request much faster.

### Cache eviction
If the cache is deleted manually or TTL expires:
- Redis removes the stored entry.
- Next request again becomes a cache miss.
- The service calls the weather API again.

---

## Important Spring Cache Annotations

### `@Cacheable`
Used on the method whose result should be stored in cache.

Example:
```java
@Cacheable(value = "weatherCache", key = "#city")
public WeatherResponse getWeather(String city) {
    ...
}
```

Meaning:
- Check cache first.
- If value exists, return it.
- If not, execute method and store result.

### `@CachePut`
Used when you want to force an update of cache.

### `@CacheEvict`
Used to remove a specific cache entry.

Example:
```java
@CacheEvict(value = "weatherCache", key = "#city")
public String clearCache(String city) {
    ...
}
```

---

## Internal Working of Caching in Spring Boot

Spring Boot caching works through **proxy-based method interception**.

### Internal flow
1. A request enters the controller.
2. Controller calls the service method.
3. Spring checks whether the method has caching annotations.
4. A proxy intercepts the method call.
5. If `@Cacheable` is present, Spring first searches the cache.
6. If the key exists, Spring returns the cached value.
7. If the key does not exist, the actual method is executed.
8. The returned result is saved in cache.
9. Future requests use the cached value.

### Why proxy matters
Spring does not directly call the method every time. It uses a proxy object around the real bean. That proxy decides whether to execute the method or return cached data.

---

## CacheManager Class Explanation

`CacheManager` is one of the most important parts of Spring caching.

### Interview-style definition
**CacheManager is the central component that manages cache creation, cache access, cache lookup, and cache lifecycle in Spring’s caching abstraction.**

### What it does
- Creates and manages cache names like `weatherCache`
- Stores cache configuration details
- Controls TTL policies
- Coordinates cache reads and writes
- Works behind annotations like `@Cacheable`, `@CachePut`, and `@CacheEvict`

### In this project
I used `RedisCacheManager`, which is a Redis-based implementation of `CacheManager`.

It helps in:
- connecting Spring Boot caching with Redis
- defining different cache TTL values
- storing cached values in Redis format

---

## CacheConfig Construction Steps

The cache configuration class is responsible for defining how Redis caching behaves in the application.

### Step 1: Enable caching
Use `@EnableCaching` so Spring activates caching support.

### Step 2: Create `RedisCacheManager`
Build a cache manager using `RedisConnectionFactory`.

### Step 3: Define serializer
Configure JSON-based serialization so cached values can be stored and read properly.

### Step 4: Set default TTL
Add a default time-to-live value for cache entries.

### Step 5: Add custom cache TTL values
Different caches can have different expiration times.

Example:
- `weatherCache` → 10 minutes
- `cityCache` → 2 minutes

### Step 6: Build the manager
Return the configured `RedisCacheManager` bean to Spring.

---

## Why TTL is Important

TTL stands for **Time-To-Live**.

### Interview-style definition
**TTL is the duration for which a cache entry is considered valid before it automatically expires and gets removed from cache.**

### Why TTL matters
- prevents stale data
- controls memory usage
- helps keep data fresh
- avoids permanently storing old weather information

Since weather data changes frequently, TTL is important for maintaining realistic results.

---

## Cache Expiration and Eviction

### Expiration
Cache entries automatically disappear after TTL ends.

### Eviction
Cache entries can also be removed manually using `@CacheEvict`.

In this project, both ideas are important:
- TTL handles automatic removal
- `@CacheEvict` supports manual deletion

---

## Controller Flow

The controller receives HTTP requests and forwards them to the service layer.

Example endpoints:
- `GET /weather/{city}` → get weather data
- `PUT /weather/{city}` → refresh cached data
- `DELETE /weather/{city}` → remove cache entry

---

## Service Layer Flow

The service layer contains the actual caching logic.

### `getWeather(city)`
- Annotated with `@Cacheable`
- Fetches data only when cache miss happens
- Parses weather API response
- Returns `WeatherResponse`

### `updateWeather(city)`
- Annotated with `@CachePut`
- Forces new data into the cache

### `clearCache(city)`
- Annotated with `@CacheEvict`
- Removes cached value for a city

---

## Redis Cloud / Local Redis Setup

This project can work with:
- **Local Redis** for development
- **Redis Cloud** for cloud-based practice and deployment-style setup

### Local Redis
- Run Redis locally
- Use `localhost` and port `6379`

### Redis Cloud
- Create Redis database from the cloud console
- Copy host, port, username, and password
- Enable SSL in Spring Boot configuration

---

## Key Learning from This Homework

This homework helped me understand:
- what caching is in backend systems
- how Spring Boot caching works internally
- how Redis is connected with Spring Boot
- how cache hit and cache miss happen
- how TTL and cache eviction work
- how to design a real-world weather API caching system

---

## Interview Questions I Can Now Answer

### 1. What is caching?
Caching is a technique to store frequently used data in fast temporary storage so future requests can be served faster.

### 2. Why use Redis for caching?
Redis is fast, memory-based, supports TTL, and is ideal for high-speed cache access.

### 3. What does `@Cacheable` do?
It checks cache before running a method and stores the result if data is missing.

### 4. What is `CacheManager`?
It is the component that manages cache creation, lookup, and lifecycle.

### 5. Why is TTL useful?
TTL automatically removes old cache data and keeps cached values fresh.

---

## Conclusion

This Week 11 homework gave me practical experience with Redis caching in Spring Boot. I learned how to reduce repeated API calls, improve performance, and configure cache expiration properly. This project is a strong backend portfolio project because it demonstrates real-world performance optimization, clean architecture, and Spring caching fundamentals.
