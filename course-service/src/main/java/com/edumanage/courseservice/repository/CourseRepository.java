package com.edumanage.courseservice.repository;

import com.edumanage.courseservice.model.Course;
import com.edumanage.courseservice.model.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    Optional<Course> findByCode(String code);
    boolean existsByCode(String code);
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByTeacherId(UUID teacherId);

    @Query("SELECT c FROM Course c WHERE c.enrolledCount < c.maxStudents AND c.status = 'ACTIVE'")
    List<Course> findAvailableCourses();
}
