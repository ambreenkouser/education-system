package com.edumanage.feeservice.repository;

import com.edumanage.feeservice.model.FeeStructure;
import com.edumanage.feeservice.model.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, UUID> {
    List<FeeStructure> findByGradeLevelAndActiveTrue(String gradeLevel);
    Optional<FeeStructure> findByGradeLevelAndFeeTypeAndActiveTrue(String gradeLevel, FeeType feeType);
}
