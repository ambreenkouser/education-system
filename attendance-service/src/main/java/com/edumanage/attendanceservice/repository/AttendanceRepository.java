package com.edumanage.attendanceservice.repository;

import com.edumanage.attendanceservice.model.AttendanceRecord;
import com.edumanage.attendanceservice.model.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, UUID> {

    List<AttendanceRecord> findByStudentId(UUID studentId);
    List<AttendanceRecord> findByCourseIdAndAttendanceDate(UUID courseId, LocalDate date);
    Optional<AttendanceRecord> findByStudentIdAndCourseIdAndAttendanceDate(
            UUID studentId, UUID courseId, LocalDate date);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a " +
           "WHERE a.studentId = :studentId AND a.courseId = :courseId AND a.status = :status")
    long countByStudentIdAndCourseIdAndStatus(UUID studentId, UUID courseId, AttendanceStatus status);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a " +
           "WHERE a.studentId = :studentId AND a.courseId = :courseId")
    long countByStudentIdAndCourseId(UUID studentId, UUID courseId);
}
