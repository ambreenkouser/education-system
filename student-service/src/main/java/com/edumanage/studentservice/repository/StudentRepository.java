package com.edumanage.studentservice.repository;

import com.edumanage.studentservice.model.Student;
import com.edumanage.studentservice.model.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByUserId(UUID userId);
    Optional<Student> findByStudentCode(String studentCode);
    boolean existsByUserId(UUID userId);
    boolean existsByStudentCode(String studentCode);
    List<Student> findByStatus(StudentStatus status);
    List<Student> findByGradeLevel(String gradeLevel);
}
