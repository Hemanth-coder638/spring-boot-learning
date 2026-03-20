package com.hemanth.collegemanagement.repository;

import com.hemanth.collegemanagement.entity.AdmissionRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdmissionRecordRepository extends JpaRepository<AdmissionRecordEntity, Long> {
}