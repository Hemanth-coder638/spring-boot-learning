Spring Security – User & Post Management API

This project is a backend-focused Spring Boot application built to demonstrate modern Spring Security concepts using JWT-based authentication and authorization.
It models a real-world scenario where authenticated users can securely create and manage posts.
The primary goal of this project is to gain hands-on, internal-level understanding of Spring Security, not just surface-level configuration.

# Project Objectives
Implement stateless authentication using JWT
Secure REST APIs using Spring Security Filter Chain
Understand authentication vs authorization clearly
Apply role-based access control for protected resources
Build a clean User–Post relationship with secured access

#Functional Scope
# User Module
User registration with encrypted passwords
User login with JWT token generation
Secure user authentication using Spring Security
User identity extracted from JWT for request processing

# Post Module
Authenticated users can create posts
Authenticated users can view posts
Posts are linked to the authenticated user
Unauthorized access is blocked at the security layer

# Security Architecture
This project uses a stateless security model.
Authentication Flow
User logs in with credentials
AuthenticationManager validates credentials
JWT token is generated on successful authentication
Token is sent with subsequent requests
Custom security filter validates JWT
SecurityContext is populated with authenticated user
Authorization
Requests pass through Spring Security Filter Chain
Only authenticated users can access protected endpoints
Endpoint-level access control enforced

# Key Spring Security Concepts Implemented
Spring Security Filter Chain
AuthenticationManager & AuthenticationProvider
UserDetailsService
PasswordEncoder (BCrypt)
SecurityContext & SecurityContextHolder
JWT token creation, validation & claims handling
Stateless session management

# Tech Stack
Spring Security
JWT (JSON Web Tokens)
Spring Data JPA
MySQL
Maven
Postman

# API Testing
APIs are tested using Postman
JWT token is passed via Authorization header
Token-based access verified for protected endpoints

# Configuration & Security Notes
Sensitive credentials (OAuth / secrets) are not committed
Environment variables are used for secure configuration
application.properties / application.yml follows best practices
target/ and IDE files are excluded via .gitignore

# How to Run the Project
Clone the repository
Configure database credentials
Set required environment variables

Run the app
