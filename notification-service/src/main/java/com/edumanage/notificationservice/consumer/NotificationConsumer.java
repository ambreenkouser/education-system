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
            log.debug("Duplicate user.created skipped userId={}", event.getUserId());
            return;
        }
        // UserCreatedEvent carries the real email — no lookup needed
        sendSafely(event.getEmail(), "Welcome to EduManage Pro!",
                "Dear user,\n\nYour account has been created successfully.\nRole: " + event.getRole() +
                "\n\nPlease log in at https://edumanage.com\n\nEduManage Team",
                eventId);
    }

    @KafkaListener(topics = "student.enrolled", groupId = "notification-service-group",
            containerFactory = "studentEnrolledKafkaListenerContainerFactory")
    public void onStudentEnrolled(StudentEnrolledEvent event) {
        String eventId = "student-enrolled:" + event.getEnrollmentId();
        if (!deduplicationService.isFirstOccurrence(eventId)) {
            log.debug("Duplicate student.enrolled skipped enrollmentId={}", event.getEnrollmentId());
            return;
        }
        // TODO: resolve actual student email via user-service Feign call using event.getStudentId()
        // Placeholder uses studentCode until user-service lookup is wired
        String toEmail = event.getStudentEmail() != null
                ? event.getStudentEmail()
                : event.getStudentCode() + "@students.edumanage.com";
        sendSafely(toEmail, "Enrollment Confirmed",
                "Dear Student " + event.getStudentCode() + ",\n\n" +
                "You have been successfully enrolled in course: " + event.getCourseId() +
                "\nEnrolled at: " + event.getEnrolledAt() + "\n\nEduManage Team",
                eventId);
    }

    @KafkaListener(topics = "attendance.marked", groupId = "notification-service-group",
            containerFactory = "attendanceMarkedKafkaListenerContainerFactory")
    public void onAttendanceMarked(AttendanceMarkedEvent event) {
        if (!"ABSENT".equals(event.getStatus())) return;

        String eventId = "attendance-marked:" + event.getAttendanceId();
        if (!deduplicationService.isFirstOccurrence(eventId)) {
            log.debug("Duplicate attendance.marked skipped attendanceId={}", event.getAttendanceId());
            return;
        }
        // TODO: resolve parent email via user-service Feign call using event.getParentId()
        String parentEmail = event.getParentEmail() != null
                ? event.getParentEmail()
                : "parent-" + event.getParentId() + "@edumanage.com";
        sendSafely(parentEmail, "Attendance Alert: Your child was absent",
                "Dear Parent,\n\nYour child (Student ID: " + event.getStudentId() + ")" +
                " was marked ABSENT on " + event.getAttendanceDate() +
                " for course: " + event.getCourseId() +
                "\n\nPlease contact the school if this is unexpected.\n\nEduManage Team",
                eventId);
    }

    @KafkaListener(topics = "grade.published", groupId = "notification-service-group",
            containerFactory = "gradePublishedKafkaListenerContainerFactory")
    public void onGradePublished(GradePublishedEvent event) {
        String eventId = "grade-published:" + event.getGradeRecordId();
        if (!deduplicationService.isFirstOccurrence(eventId)) {
            log.debug("Duplicate grade.published skipped gradeRecordId={}", event.getGradeRecordId());
            return;
        }
        // TODO: resolve student email via user-service Feign call using event.getStudentId()
        String studentEmail = event.getStudentEmail() != null
                ? event.getStudentEmail()
                : event.getStudentId() + "@students.edumanage.com";
        sendSafely(studentEmail, "Grade Published: " + event.getExamTitle(),
                "Dear Student,\n\nYour grade for exam '" + event.getExamTitle() + "' has been published.\n" +
                "Marks Obtained: " + event.getMarksObtained() + "\nGrade: " + event.getGradeLetter() +
                "\n\nEduManage Team",
                eventId);
    }

    @KafkaListener(topics = "fee.paid", groupId = "notification-service-group",
            containerFactory = "feePaidKafkaListenerContainerFactory")
    public void onFeePaid(FeePaidEvent event) {
        String eventId = "fee-paid:" + event.getPaymentId();
        if (!deduplicationService.isFirstOccurrence(eventId)) {
            log.debug("Duplicate fee.paid skipped paymentId={}", event.getPaymentId());
            return;
        }
        // TODO: resolve student email via user-service Feign call using event.getStudentId()
        String studentEmail = event.getStudentEmail() != null
                ? event.getStudentEmail()
                : event.getStudentId() + "@students.edumanage.com";
        sendSafely(studentEmail, "Payment Receipt - EduManage",
                "Dear Student,\n\nYour payment has been received.\n" +
                "Amount Paid: " + event.getPaidAmount() + "\n" +
                "Payment Method: " + event.getPaymentMethod() + "\n" +
                "Transaction ID: " + event.getTransactionId() + "\n" +
                "Date: " + event.getPaidAt() + "\n\nThank you!\nEduManage Team",
                eventId);
    }

    /**
     * Wraps every email send in try-catch.
     * On failure: logs full stack trace and re-throws so Kafka's DefaultErrorHandler
     * can route the message to the dead-letter topic (dlq.notifications).
     */
    private void sendSafely(String to, String subject, String body, String eventId) {
        try {
            emailService.sendEmail(to, subject, body);
        } catch (Exception e) {
            log.error("Failed to send notification eventId={} to={} subject='{}': {}",
                    eventId, to, subject, e.getMessage(), e);
            throw e;  // propagate so DLQ error handler captures the message
        }
    }
}
