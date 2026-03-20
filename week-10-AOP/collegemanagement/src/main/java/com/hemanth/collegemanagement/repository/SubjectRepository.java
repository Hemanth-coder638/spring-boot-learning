package com.hemanth.collegemanagement.repository;

import com.hemanth.collegemanagement.entity.SubjectEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {
    @EntityGraph(attributePaths = {"professor", "studentEntities"})
    Optional<SubjectEntity> findWithRelationsById(Long id);
}