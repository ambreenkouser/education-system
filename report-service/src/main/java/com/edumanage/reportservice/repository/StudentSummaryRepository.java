package com.edumanage.reportservice.repository;

import com.edumanage.reportservice.model.StudentSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StudentSummaryRepository extends JpaRepository<StudentSummary, UUID> {
}
