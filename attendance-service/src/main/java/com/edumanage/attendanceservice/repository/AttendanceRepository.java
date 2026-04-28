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

    /**
     * Single-query aggregate replacing 4 separate COUNT calls.
     * Returns [total, present, absent, late] in one DB round-trip.
     */
    @Query("""
           SELECT COUNT(a),
                  SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END),
                  SUM(CASE WHEN a.status = 'ABSENT'  THEN 1 ELSE 0 END),
                  SUM(CASE WHEN a.status = 'LATE'    THEN 1 ELSE 0 END)
           FROM AttendanceRecord a
           WHERE a.studentId = :studentId AND a.courseId = :courseId
           """)
    Object[] countSummary(UUID studentId, UUID courseId);
}
