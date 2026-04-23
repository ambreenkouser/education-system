package com.edumanage.gradeservice.repository;

import com.edumanage.gradeservice.model.GradeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GradeRecordRepository extends JpaRepository<GradeRecord, UUID> {

    List<GradeRecord> findByStudentId(UUID studentId);

    @Query("SELECT gr FROM GradeRecord gr JOIN gr.exam e WHERE e.courseId = :courseId AND gr.studentId = :studentId")
    List<GradeRecord> findByStudentIdAndCourseId(UUID studentId, UUID courseId);

    Optional<GradeRecord> findByStudentIdAndExamId(UUID studentId, UUID examId);

    @Query("SELECT AVG(gr.gradePoints) FROM GradeRecord gr WHERE gr.studentId = :studentId")
    Optional<Double> calculateGpa(UUID studentId);
}
