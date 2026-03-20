package com.hemanth.collegemanagement.repository;

import com.hemanth.collegemanagement.entity.StudentEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
  //Use EntityGraph to load associated collections and avoid n+1 when fetching a student with relations
    @EntityGraph(attributePaths = {"professorEntities", "subjectEntities", "admissionRecord"})
    Optional<StudentEntity> findWithRelationsById(Long id);
}