package com.edumanage.notificationservice.consumer;

import com.edumanage.notificationservice.event.*;
import com.edumanage.notificationservice.service.DeduplicationService;
import com.edumanage.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;
    private final DeduplicationService deduplicationService;

    @KafkaListener(topics = "user.created", groupId = "notification-service-group",
            containerFactory = "userCreatedKafkaListenerContainerFactory")
    public void onUserCreated(UserCreatedEvent event) {
        String eventId = "user-created:" + event.getUserId();
        if (!deduplicationService.isFirstOccurrence(eventId)) {
            log.debug("Duplicate user.created event skipped: {}", event.getUserId());
            return;
        }
        emailService.sendEmail(
                event.getEmail(),
                "Welcome to EduManage Pro!",
                "Dear user,\n\nYour account has been created successfully.\nRole: " + event.getRole() +
                        "\n\nPlease log in at https://edumanage.com\n\nEduManage Team"
        );
    }

    @KafkaListener(topics = "student.enrolled", groupId = "notification-service-group",
            containerFactory = "studentEnrolledKafkaListenerContainerFactory")
    public void onStudentEnrolled(StudentEnrolledEvent event) {
        String eventId = "student-enrolled:" + event.getEnrollmentId();
        if (!deduplicationService.isFirstOccurrence(eventId)) {
            log.debug("Duplicate student.enrolled event skipped: {}", event.getEnrollmentId());
            return;
        }
        // In a real system, look up student email via user-service Feign call.
        // Here we log and simulate the send.
        log.info("Sending enrollment confirmation for student={} course={}",
                event.getStudentId(), event.getCourseId());
        emailService.sendEmail(
                event.getStudentCode() + "@students.edumanage.com",
                "Enrollment Confirmed",
                "Dear Student " + event.getStudentCode() + ",\n\n" +
                        "You have been successfully enrolled in course: " + event.getCourseId() +
                        "\nEnrolled at: " + event.getEnrolledAt() +
                        "\n\nEduManage Team"
        );
    }

    @KafkaListener(topics = "attendance.marked", groupId = "notification-service-group",
            containerFactory = "attendanceMarkedKafkaListenerContainerFactory")
    public void onAttendanceMarked(AttendanceMarkedEvent event) {
        if (!"ABSENT".equals(event.getStatus())) {
            return;
        }
        String eventId = "attendance-marked:" + event.getAttendanceId();
        if (!deduplicationService.isFirstOccurrence(eventId)) {
            log.debug("Duplicate attendance.marked event skipped: {}", event.getAttendanceId());
            return;
        }
        log.info("Sending absent alert for student={} date={}", event.getStudentId(), event.getAttendanceDate());
        emailService.sendEmail(
                "parent-" + event.getParentId() + "@edumanage.com",
                "Attendance Alert: Your child was absent",
                "Dear Parent,\n\nYour child (Student ID: " + event.getStudentId() + ")" +
                        " was marked ABSENT on " + event.getAttendanceDate() +
                        " for course: " + event.getCourseId() +
                        "\n\nPlease contact the school if this is unexpected.\n\nEduManage Team"
        );
    }

    @KafkaListener(topics = "grade.published", groupId = "notification-service-group",
            containerFactory = "gradePublishedKafkaListenerContainerFactory")
    public void onGradePublished(GradePublishedEvent event) {
        String eventId = "grade-published:" + event.getGradeRecordId();
        if (!deduplicationService.isFirstOccurrence(eventId)) {
            log.debug("Duplicate grade.published event skipped: {}", event.getGradeRecordId());
            return;
        }
        log.info("Sending grade notification for student={} exam={}", event.getStudentId(), event.getExamTitle());
        emailService.sendEmail(
                event.getStudentId() + "@students.edumanage.com",
                "Grade Published: " + event.getExamTitle(),
                "Dear Student,\n\nYour grade for exam '" + event.getExamTitle() + "' has been published.\n" +
                        "Marks Obtained: " + event.getMarksObtained() + "\n" +
                        "Grade: " + event.getGradeLetter() +
                        "\n\nEduManage Team"
        );
    }

    @KafkaListener(topics = "fee.paid", groupId = "notification-service-group",
            containerFactory = "feePaidKafkaListenerContainerFactory")
    public void onFeePaid(FeePaidEvent event) {
        String eventId = "fee-paid:" + event.getPaymentId();
        if (!deduplicationService.isFirstOccurrence(eventId)) {
            log.debug("Duplicate fee.paid event skipped: {}", event.getPaymentId());
            return;
        }
        log.info("Sending payment receipt for student={} txn={}", event.getStudentId(), event.getTransactionId());
        emailService.sendEmail(
                event.getStudentId() + "@students.edumanage.com",
                "Payment Receipt - EduManage",
                "Dear Student,\n\nYour payment has been received.\n" +
                        "Amount Paid: " + event.getPaidAmount() + "\n" +
                        "Payment Method: " + event.getPaymentMethod() + "\n" +
                        "Transaction ID: " + event.getTransactionId() + "\n" +
                        "Date: " + event.getPaidAt() +
                        "\n\nThank you!\nEduManage Team"
        );
    }
}
