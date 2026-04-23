package com.edumanage.attendanceservice;

import com.edumanage.attendanceservice.dto.AttendanceRequest;
import com.edumanage.attendanceservice.dto.AttendanceSummary;
import com.edumanage.attendanceservice.event.AttendanceMarkedEvent;
import com.edumanage.attendanceservice.mapper.AttendanceMapper;
import com.edumanage.attendanceservice.model.AttendanceRecord;
import com.edumanage.attendanceservice.model.AttendanceStatus;
import com.edumanage.attendanceservice.repository.AttendanceRepository;
import com.edumanage.attendanceservice.service.AttendanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock AttendanceRepository attendanceRepository;
    @Mock AttendanceMapper attendanceMapper;
    @Mock KafkaTemplate<String, AttendanceMarkedEvent> kafkaTemplate;
    @InjectMocks AttendanceService attendanceService;

    @Test
    void mark_whenAlreadyMarked_throwsIllegalArgument() {
        UUID studentId = UUID.randomUUID();
        UUID courseId  = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        AttendanceRequest req = new AttendanceRequest();
        req.setStudentId(studentId);
        req.setCourseId(courseId);
        req.setAttendanceDate(date);
        req.setStatus(AttendanceStatus.PRESENT);

        when(attendanceRepository.findByStudentIdAndCourseIdAndAttendanceDate(studentId, courseId, date))
                .thenReturn(Optional.of(AttendanceRecord.builder().build()));

        assertThatThrownBy(() -> attendanceService.mark(req, UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already marked");
    }

    @Test
    void getSummary_calculatesCorrectPercentage() {
        UUID studentId = UUID.randomUUID();
        UUID courseId  = UUID.randomUUID();

        when(attendanceRepository.countByStudentIdAndCourseId(studentId, courseId)).thenReturn(10L);
        when(attendanceRepository.countByStudentIdAndCourseIdAndStatus(studentId, courseId, AttendanceStatus.PRESENT)).thenReturn(8L);
        when(attendanceRepository.countByStudentIdAndCourseIdAndStatus(studentId, courseId, AttendanceStatus.ABSENT)).thenReturn(2L);
        when(attendanceRepository.countByStudentIdAndCourseIdAndStatus(studentId, courseId, AttendanceStatus.LATE)).thenReturn(0L);

        AttendanceSummary summary = attendanceService.getSummary(studentId, courseId);

        assertThat(summary.getAttendancePercentage()).isEqualTo(80.0);
        assertThat(summary.getPresentCount()).isEqualTo(8L);
        assertThat(summary.getAbsentCount()).isEqualTo(2L);
    }
}
