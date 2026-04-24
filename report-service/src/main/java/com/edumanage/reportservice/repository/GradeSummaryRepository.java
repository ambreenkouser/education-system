package com.edumanage.reportservice.repository;

import com.edumanage.reportservice.model.GradeSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface GradeSummaryRepository extends JpaRepository<GradeSummary, UUID> {
    List<GradeSummary> findByStudentId(UUID studentId);
}
