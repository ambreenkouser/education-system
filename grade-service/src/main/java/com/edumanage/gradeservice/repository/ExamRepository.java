package com.edumanage.gradeservice.repository;

import com.edumanage.gradeservice.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExamRepository extends JpaRepository<Exam, UUID> {
    List<Exam> findByCourseId(UUID courseId);
}
