package com.edumanage.reportservice.consumer;

import com.edumanage.reportservice.event.*;
import com.edumanage.reportservice.model.*;
import com.edumanage.reportservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportEventConsumer {

    private final StudentSummaryRepository studentSummaryRepository;
    private final AttendanceSummaryRepository attendanceSummaryRepository;
    private final GradeSummaryRepository gradeSummaryRepository;
    private final FeeSummaryRepository feeSummaryRepository;

    @KafkaListener(topics = "student.enrolled", groupId = "report-service-group",
            containerFactory = "studentEnrolledReportFactory")
    @Transactional
    public void onStudentEnrolled(StudentEnrolledEvent event) {
        log.info("Report: student.enrolled studentId={}", event.getStudentId());
        StudentSummary summary = studentSummaryRepository.findById(event.getStudentId())
                .orElseGet(() -> StudentSummary.builder()
                        .studentId(event.getStudentId())
                        .studentCode(event.getStudentCode())
                        .totalEnrollments(0)
                        .build());
        summary.setTotalEnrollments(summary.getTotalEnrollments() + 1);
        summary.setLastUpdated(LocalDateTime.now());
        studentSummaryRepository.save(summary);
    }

    @KafkaListener(topics = "attendance.marked", groupId = "report-service-group",
            containerFactory = "attendanceMarkedReportFactory")
    @Transactional
    public void onAttendanceMarked(AttendanceMarkedEvent event) {
        log.info("Report: attendance.marked studentId={} status={}", event.getStudentId(), event.getStatus());
        AttendanceSummary summary = attendanceSummaryRepository
                .findByStudentIdAndCourseId(event.getStudentId(), event.getCourseId())
                .orElseGet(() -> AttendanceSummary.builder()
                        .studentId(event.getStudentId())
                        .courseId(event.getCourseId())
                        .totalClasses(0).presentCount(0).absentCount(0).lateCount(0)
                        .build());
        summary.setTotalClasses(summary.getTotalClasses() + 1);
        switch (event.getStatus()) {
            case "PRESENT" -> summary.setPresentCount(summary.getPresentCount() + 1);
            case "ABSENT"  -> summary.setAbsentCount(summary.getAbsentCount() + 1);
            case "LATE"    -> summary.setLateCount(summary.getLateCount() + 1);
        }
        summary.setLastUpdated(LocalDateTime.now());
        attendanceSummaryRepository.save(summary);
    }

    @KafkaListener(topics = "grade.published", groupId = "report-service-group",
            containerFactory = "gradePublishedReportFactory")
    @Transactional
    public void onGradePublished(GradePublishedEvent event) {
        log.info("Report: grade.published studentId={} grade={}", event.getStudentId(), event.getGradeLetter());
        GradeSummary summary = GradeSummary.builder()
                .studentId(event.getStudentId())
                .examId(event.getExamId())
                .examTitle(event.getExamTitle())
                .marksObtained(event.getMarksObtained())
                .gradeLetter(event.getGradeLetter())
                .gradePoints(event.getGradePoints())
                .gradedAt(LocalDateTime.now())
                .build();
        gradeSummaryRepository.save(summary);
    }

    @KafkaListener(topics = "fee.paid", groupId = "report-service-group",
            containerFactory = "feePaidReportFactory")
    @Transactional
    public void onFeePaid(FeePaidEvent event) {
        log.info("Report: fee.paid studentId={} amount={}", event.getStudentId(), event.getPaidAmount());
        FeeSummary summary = FeeSummary.builder()
                .studentId(event.getStudentId())
                .invoiceId(event.getInvoiceId())
                .paidAmount(event.getPaidAmount())
                .paymentMethod(event.getPaymentMethod())
                .transactionId(event.getTransactionId())
                .paidAt(event.getPaidAt())
                .build();
        feeSummaryRepository.save(summary);
    }
}
