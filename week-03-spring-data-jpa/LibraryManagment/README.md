:

📚 Library Management – Spring Data JPA Module
This module deep-dives into Spring Data JPA, focusing on entity design, relationships, performance tuning, and optimized data retrieval.

🚀 Key Concepts Covered
1. JPA Entity Relationships
One-to-One
One-to-Many
Many-to-One
Many-to-Many
Bidirectional vs Unidirectional
Using mappedBy correctly
Owning side vs Inverse side

2. JPA Query Optimization
Why the N+1 Problem happens
How to fix using:
@EntityGraph
JOIN FETCH
Custom JPQL queries
Optimized fetch strategies

3. Grouped & Joined Data Retrieval
JPQL joins
Fetch joins
Group By queries
Custom projections
DTO-based queries
Mapping relationships efficiently

4. Practical Features Implemented
Author — Book — Category relationship setup
Cascade operations
Pagination and sorting
DTO layer for response optimization
Exception handling for missing entities

🏗️ Tech Stack
Java
Spring Boot
Spring Data JPA
Hibernate
H2 / MySQL
Maven

🧪 Testing
All APIs tested using Postman:
*Create Author
*Create Book
Fetch Books with Author & Category
Optimized list endpoints using JOIN FETCH

🎯 Learning Outcomes
By completing this module, you understand how real enterprise applications handle:
Relationship-heavy data models
High-performance JPA queries
Avoiding N+1 performance bottlenecks

Clean, scalable JPA architecture
