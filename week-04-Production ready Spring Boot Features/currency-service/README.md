Currency Exchange Service – Production Ready Spring Boot

This project is a production-ready Currency Exchange Service built using Spring Boot.
It demonstrates how real backend systems interact with external services while maintaining observability, documentation, and maintainability.

Features Implemented:-
External API Communication:
Integration with third-party currency exchange APIs
Implemented using Spring RestClient
Clean separation between controller, service, and client layers

RESTful API Design:
Standard HTTP methods (GET)
Meaningful request/response models
Proper HTTP status codes
DTO-based responses

Production-Ready Capabilities:-
Logging:
Centralized logging using SLF4J
Log levels configured for development and debugging
Clean log messages for tracing API flow

Spring Boot Actuator:
Health endpoint
Application metrics
Readiness & liveness probes
Enables monitoring in real deployments

API Documentation (Swagger / OpenAPI):
Auto-generated API documentation
Easy endpoint exploration via Swagger UI
Improves developer experience and API clarity

 Auditing:
Automatic tracking of:
Created timestamps
Updated timestamps
Demonstrates real-world data accountability using JPA auditing

 Testing:
APIs tested using Postman
External API behavior validated
Error handling verified for invalid inputs


Spring Boot
