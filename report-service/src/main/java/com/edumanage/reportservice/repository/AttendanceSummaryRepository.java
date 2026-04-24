package com.edumanage.reportservice.repository;

import com.edumanage.reportservice.model.AttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceSummaryRepository extends JpaRepository<AttendanceSummary, UUID> {
    List<AttendanceSummary> findByCourseId(UUID courseId);
    Optional<AttendanceSummary> findByStudentIdAndCourseId(UUID studentId, UUID courseId);
}
