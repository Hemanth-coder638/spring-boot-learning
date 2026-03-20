# 🎓 College Management System (Spring Boot + AOP)

# 📘 Aspect-Oriented Programming (AOP) – Core Concepts & Definitions

## Concepts Table

| Concept | Definition | Used In Project | Purpose |
|--------|------------|----------------|---------|
| Aspect | A class that contains cross-cutting concerns (like logging, security, validation) | SecurityAspect, PerformanceAspect, ValidationAspect | Separates common logic from business logic |
| Advice | Action taken by an aspect at a specific join point | `@Before`, `@After`, `@Around`, `@AfterThrowing` | Defines what to execute |
| Join Point | A point during execution of a program (method execution in Spring AOP) | All service layer methods | Where AOP logic can be applied |
| Pointcut | Expression that selects which join points to apply advice to | `execution(* service..*(..))` | Defines where to apply AOP |
| Target Object | The actual business class being advised | ProfessorServiceImpl, StudentServiceImpl | Contains core business logic |
| Proxy | Object created by Spring to apply AOP (wraps target object) | Spring CGLIB Proxy | Intercepts method calls and applies advice |
| Weaving | Process of linking aspects with target objects | Runtime (Spring AOP) | Combines AOP logic with application |

---

## ⚙️ Types of Advice Used in This Project

| Advice Type | Definition | Annotation | Project Usage |
|------------|-----------|------------|---------------|
| Before Advice | Executes before the method execution | `@Before` | Used for logging and input validation |
| After Advice | Executes after method execution (regardless of outcome) | `@After` | Used for logging method exit |
| Around Advice | Executes before and after method, controls execution | `@Around` | Used for security (token validation) and performance tracking |
| AfterThrowing Advice | Executes when method throws an exception | `@AfterThrowing` | Used for global exception handling |

---

## 🔄 AOP Execution Flow (Simplified)

```
Client Request
     ↓
Spring Proxy (AOP)
     ↓
@Before (Logging / Validation)
     ↓
@Around (Security Check)
     ↓
Target Method Execution
     ↓
@After (Logging)
     ↓
@AfterThrowing (if exception occurs)
     ↓
Response Returned
```


## Overview

This project is a Spring Boot-based College Management System designed to manage:

- Students  
- Professors  
- Subjects  
- Admission Records  

The core highlight of this project is the implementation of Aspect-Oriented Programming (AOP) to handle cross-cutting concerns like:

- Global Exception Handling  
- Logging  
- Security Validation  
- Performance Monitoring  
- Data Validation  

---

## Why AOP in this Project?

In a real-world backend system, certain logic is repeated across multiple layers:

- Logging method calls  
- Validating inputs  
- Checking authentication  
- Handling exceptions  

Instead of writing this logic in every method, AOP allows us to separate it cleanly, making the code:

✔ Cleaner  
✔ Reusable  
✔ Maintainable  
✔ Scalable  

---

## AOP Implementation (Homework-wise Explanation)

---

## Homework 1: Global Exception Handling

### Objective

Handle exceptions centrally without writing try-catch blocks in every method.

### Implementation

Used:

- `@AfterThrowing`

### How it works

- Whenever a service method throws an exception  
- AOP intercepts it automatically  
- Logs details like:
  - Method name  
  - Exception type  
  - Message  

### Example Output

```
Exception occurred in method: getProfessor
Exception Type: EntityNotFoundException
Message: Professor not found 100
```

### Benefit

- No need to write exception handling everywhere  
- Centralized error tracking  

---

## Homework 2: Security + Logging

### Objective

- Log method execution  
- Validate a security token before executing business logic  

### Implementation

#### Logging

Used:

- `@Before`  
- `@After`  

Logs:

- Method entry  
- Method exit  
- Arguments  

#### Security

Used:

- `@Around`

#### Logic

- Extract "token" from method arguments  
- Validate it  

```
if (token == null || !token.equals("VALID_TOKEN")) {
    throw new RuntimeException("Invalid or Missing Token");
}
```

### Example Output

```
ENTERING METHOD:
Class: ProfessorServiceImpl
Method: createProfessor

Checking security token...

EXITING METHOD: createProfessor
```

### Benefit

- Security logic is centralized  
- No need to check token in every method  

---

## Homework 3: Method Execution Time Tracking

### Objective

Measure how long each method takes to execute.

### Implementation

Created custom annotation:

- `@TrackExecutionTime`

Used:

- `@Around`

### How it works

1. Start timer before method execution  
2. Execute method  
3. Stop timer  
4. Store execution time  

### Example Output

```
Method: ProfessorServiceImpl.createProfessor(..) took 96 ms

========= PERFORMANCE REPORT =========
ProfessorServiceImpl.createProfessor(..) -> 96 ms
======================================
```

### Benefit

- Helps identify slow methods  
- Useful for performance optimization  

---

## Homework 4: Data Validation

### Objective

Validate incoming DTO data automatically before service execution.

### Implementation

Created custom annotation:

- `@ValidateDto`

Used:

- `@Before`

### Validation Logic

#### StudentDto

- Name should not be null or empty  
- IDs should not contain null values  

#### ProfessorDto

- Title should not be empty  

### Example

```
{
  "name": "   "
}
```

Output:

```
Student name cannot be null or empty
```

### Benefit

- Clean service layer (no validation code inside methods)  
- Reusable validation logic  
- Works across multiple DTOs  

---

## AOP Flow in This Project

```
Controller
   ↓
DTO (Input)
   ↓
AOP (Validation / Security / Logging / Timing)
   ↓
Service Layer
   ↓
Repository Layer
   ↓
Database
```

---

## Tech Stack

- Java 17  
- Spring Boot  
- Spring Data JPA  
- Hibernate  
- PostgreSQL  
- Spring AOP  

---

## Sample Console Flow

```
ENTERING METHOD:
Method: createProfessor

Checking security token...

select nextval('professor_entity_seq')

Method: ProfessorServiceImpl.createProfessor(..) took 96 ms

EXITING METHOD: createProfessor
```

---

## Challenges Faced

### ID not generating

- Cause: Transaction not committed  
- Fix: Added `@Transactional`  

### Data not inserting

- Cause: Mixing repository + service  
- Fix: Used service layer consistently  

### DTO to Entity mapping issues

- Cause: ModelMapper ignoring relationships  
- Fix: Manual mapping for relationships  

### AOP not triggering

- Cause: Direct method calls / wrong proxy usage  
- Fix: Used Spring-managed beans  

---

## Key Learnings

- How Spring AOP works internally using proxies  
- Difference between `@Before`, `@After`, `@Around`, `@AfterThrowing`  
- Importance of transaction management  
- DTO vs Entity separation  
- Real-world debugging of JPA + AOP issues  

---

## Future Improvements

- Replace manual validation with `@Valid` and Hibernate Validator  
- Implement real JWT-based authentication  
- Add API documentation using Swagger  
- Improve performance reporting with aggregation  

---

## Conclusion

This project demonstrates how AOP can transform a basic CRUD application into a clean, scalable, and production-ready system by handling cross-cutting concerns in a centralized manner.

---

---

## 🧠 Key Takeaway

**AOP allows you to write clean business logic by moving repetitive concerns like logging, validation, security, and performance tracking into separate reusable components called aspects.**
