package com.codingshuttle.springbootwebtutorial.springbootwebtutorial.repositories;

import com.codingshuttle.springbootwebtutorial.springbootwebtutorial.entities.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//Why DepartmentRepository is an interface and not a class
//Spring Data JPA follows a “Programming to Interface” strategy.
//
//You write just the interface:
//@Repository
//public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {
//}
//You do NOT write any implementation like DepartmentRepositoryImpl.
//Because…
//🚀 Spring auto-generates the implementation at runtime

//When your project starts, Spring scans for repositories that extend JpaRepository.
//Then Spring dynamically creates a proxy class behind the scenes — like:
//class DepartmentRepository$$SpringProxy implements DepartmentRepository {
//    // Implementation of CRUD methods for DepartmentEntity
//}
//This auto-generated class is the real object stored inside your reference variable:
//private final DepartmentRepository departmentRepository;
//So technically:
//Reference Type → Interface (DepartmentRepository)
//Actual Object → Spring-generated class (DepartmentRepository$$SpringProxy)
//You can’t see this class in your code — Spring builds it using bytecode generation.
@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

}
