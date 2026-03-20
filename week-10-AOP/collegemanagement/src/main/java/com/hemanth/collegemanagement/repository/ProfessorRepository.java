package com.hemanth.collegemanagement.repository;

import com.hemanth.collegemanagement.entity.ProfessorEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<ProfessorEntity, Long> {
   //fetch subjects and students together
    @EntityGraph(attributePaths = {"subjectEntities", "studentEntities"})
    Optional<ProfessorEntity> findWithRelationsById(Long id);
}