package com.edumanage.attendanceservice.service;

import com.edumanage.attendanceservice.dto.AttendanceRequest;
import com.edumanage.attendanceservice.dto.AttendanceResponse;
import com.edumanage.attendanceservice.dto.AttendanceSummary;
import com.edumanage.attendanceservice.event.AttendanceMarkedEvent;
import com.edumanage.attendanceservice.mapper.AttendanceMapper;
import com.edumanage.attendanceservice.model.AttendanceRecord;
import com.edumanage.attendanceservice.model.AttendanceStatus;
import com.edumanage.attendanceservice.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;
    private final KafkaTemplate<String, AttendanceMarkedEvent> kafkaTemplate;

    private static final String ATTENDANCE_MARKED_TOPIC = "attendance.marked";

    @Transactional
    @CacheEvict(value = "attendance", key = "#request.courseId + ':' + #request.attendanceDate")
    public AttendanceResponse mark(AttendanceRequest request, UUID markedBy) {
        attendanceRepository.findByStudentIdAndCourseIdAndAttendanceDate(
                request.getStudentId(), request.getCourseId(), request.getAttendanceDate())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Attendance already marked for student " + request.getStudentId()
                            + " on " + request.getAttendanceDate());
                });

        AttendanceRecord record = AttendanceRecord.builder()
                .studentId(request.getStudentId())
                .courseId(request.getCourseId())
                .attendanceDate(request.getAttendanceDate())
                .status(request.getStatus())
                .markedBy(markedBy)
                .remarks(request.getRemarks())
                .build();

        AttendanceRecord saved = attendanceRepository.save(record);

        // Publish event — notification-service sends absent alert to parent
        kafkaTemplate.send(ATTENDANCE_MARKED_TOPIC, request.getStudentId().toString(),
                AttendanceMarkedEvent.builder()
                        .recordId(saved.getId())
                        .studentId(request.getStudentId())
                        .courseId(request.getCourseId())
                        .attendanceDate(request.getAttendanceDate())
                        .status(request.getStatus().name())
                        .build());

        log.info("Attendance marked: studentId={}, status={}", request.getStudentId(), request.getStatus());
        return attendanceMapper.toResponse(saved);
    }

    public List<AttendanceResponse> getByStudent(UUID studentId) {
        return attendanceRepository.findByStudentId(studentId).stream()
                .map(attendanceMapper::toResponse).toList();
    }

    @Cacheable(value = "attendance", key = "#courseId + ':' + #date")
    public List<AttendanceResponse> getByCourseAndDate(UUID courseId, LocalDate date) {
        return attendanceRepository.findByCourseIdAndAttendanceDate(courseId, date).stream()
                .map(attendanceMapper::toResponse).toList();
    }

    public AttendanceSummary getSummary(UUID studentId, UUID courseId) {
        long total   = attendanceRepository.countByStudentIdAndCourseId(studentId, courseId);
        long present = attendanceRepository.countByStudentIdAndCourseIdAndStatus(
                studentId, courseId, AttendanceStatus.PRESENT);
        long absent  = attendanceRepository.countByStudentIdAndCourseIdAndStatus(
                studentId, courseId, AttendanceStatus.ABSENT);
        long late    = attendanceRepository.countByStudentIdAndCourseIdAndStatus(
                studentId, courseId, AttendanceStatus.LATE);

        double percentage = total == 0 ? 0.0 : (double)(present + late) / total * 100.0;

        return AttendanceSummary.builder()
                .studentId(studentId).courseId(courseId)
                .totalClasses(total).presentCount(present)
                .absentCount(absent).lateCount(late)
                .attendancePercentage(Math.round(percentage * 100.0) / 100.0)
                .build();
    }
}
