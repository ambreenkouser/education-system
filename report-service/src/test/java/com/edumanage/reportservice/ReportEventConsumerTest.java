package com.edumanage.reportservice;

import com.edumanage.reportservice.consumer.ReportEventConsumer;
import com.edumanage.reportservice.event.StudentEnrolledEvent;
import com.edumanage.reportservice.event.AttendanceMarkedEvent;
import com.edumanage.reportservice.model.AttendanceSummary;
import com.edumanage.reportservice.model.StudentSummary;
import com.edumanage.reportservice.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportEventConsumerTest {

    @Mock StudentSummaryRepository studentSummaryRepository;
    @Mock AttendanceSummaryRepository attendanceSummaryRepository;
    @Mock GradeSummaryRepository gradeSummaryRepository;
    @Mock FeeSummaryRepository feeSummaryRepository;
    @InjectMocks ReportEventConsumer consumer;

    @Test
    void onStudentEnrolled_createsNewSummary() {
        UUID studentId = UUID.randomUUID();
        StudentEnrolledEvent event = new StudentEnrolledEvent();
        event.setStudentId(studentId);
        event.setStudentCode("STU-001");
        event.setEnrolledAt(LocalDateTime.now());

        when(studentSummaryRepository.findById(studentId)).thenReturn(Optional.empty());
        when(studentSummaryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        consumer.onStudentEnrolled(event);

        verify(studentSummaryRepository).save(argThat(s ->
                s.getTotalEnrollments() == 1 && "STU-001".equals(s.getStudentCode())));
    }

    @Test
    void onStudentEnrolled_incrementsExistingSummary() {
        UUID studentId = UUID.randomUUID();
        StudentSummary existing = StudentSummary.builder()
                .studentId(studentId).studentCode("STU-001").totalEnrollments(2)
                .lastUpdated(LocalDateTime.now()).build();

        StudentEnrolledEvent event = new StudentEnrolledEvent();
        event.setStudentId(studentId);
        event.setStudentCode("STU-001");
        event.setEnrolledAt(LocalDateTime.now());

        when(studentSummaryRepository.findById(studentId)).thenReturn(Optional.of(existing));
        when(studentSummaryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        consumer.onStudentEnrolled(event);

        verify(studentSummaryRepository).save(argThat(s -> s.getTotalEnrollments() == 3));
    }

    @Test
    void onAttendanceMarked_absent_incrementsAbsentCount() {
        UUID studentId = UUID.randomUUID();
        UUID courseId  = UUID.randomUUID();

        AttendanceMarkedEvent event = new AttendanceMarkedEvent();
        event.setAttendanceId(UUID.randomUUID());
        event.setStudentId(studentId);
        event.setCourseId(courseId);
        event.setAttendanceDate(LocalDate.now());
        event.setStatus("ABSENT");

        when(attendanceSummaryRepository.findByStudentIdAndCourseId(studentId, courseId))
                .thenReturn(Optional.empty());
        when(attendanceSummaryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        consumer.onAttendanceMarked(event);

        verify(attendanceSummaryRepository).save(argThat(s ->
                s.getTotalClasses() == 1 && s.getAbsentCount() == 1 && s.getPresentCount() == 0));
    }
}
