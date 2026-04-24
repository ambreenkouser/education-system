package com.edumanage.reportservice.repository;

import com.edumanage.reportservice.model.FeeSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface FeeSummaryRepository extends JpaRepository<FeeSummary, UUID> {
    List<FeeSummary> findByStudentId(UUID studentId);
}
