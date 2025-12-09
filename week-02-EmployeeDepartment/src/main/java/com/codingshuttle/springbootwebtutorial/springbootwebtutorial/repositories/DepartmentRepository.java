package com.codingshuttle.springbootwebtutorial.springbootwebtutorial.repositories;

import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.entities.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
//DepartmentRepository is declared as interface not as class why because
//JpaRepository is an interface, not a base class.
//→ You can’t extend an interface using a class.
//→ A class must implement the interface and provide implementations for every method.
//
//2️⃣ You would be forced to write all CRUD logic manually
//→ save(), findAll(), delete(), etc.
//→ Completely defeats the purpose of Spring Data JPA automation.
//
//3️⃣ Spring cannot generate dynamic proxy implementation for a class
//→ It needs an interface contract to create a proxy instance.

//What is job @Repository
//The @Repository annotation is a specialized stereotype in Spring used to mark a class (or auto-detected interface implementation) as a Data Access Layer component.
//
//It plays 3 strategic roles:
//
//1️⃣ It Registers the Repository as a Spring Bean
//
//Spring auto-detects and manages the object lifecycle.
//
//➡️ Enables Dependency Injection
//➡️ You can @Autowired it into services
//
//@Repository
//public interface EmployeeRepository extends JpaRepository<Employee, Long> {}
//
//2️⃣ Exception Translation — The Real Magic
//
//Spring wraps low-level database errors (JPA/JDBC exceptions) into Spring’s unified exception hierarchy:
//
//Raw Exception (JPA/Hibernate)	Spring Exception
//PersistenceException	DataAccessException
//SQLException	DataIntegrityViolationException
//
//➡️ Cleaner, database-agnostic error handling
//➡️ Avoids leaking vendor-specific exception types
//
//3️⃣ Indicates the Layer’s Semantics
//
//A communication tool — a badge saying:
//
//“This class handles DB operations, nothing else.”
//
//It improves:
//
//Maintainability
//
//Layered architecture clarity
//
//Tooling and future migrations
//
//🧠 Behind the Scenes
//
//Spring AOP (Aspect-Oriented Programming) applies a proxy around methods in a @Repository bean to perform:
//
//✔ Transaction participation
//✔ Resource management
//✔ Declarative exception translation

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity,Long> {

}
