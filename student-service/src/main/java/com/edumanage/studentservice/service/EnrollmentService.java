package com.edumanage.studentservice.service;

import com.edumanage.studentservice.client.CourseServiceClient;
import com.edumanage.studentservice.dto.EligibilityResponse;
import com.edumanage.studentservice.dto.EnrollmentRequest;
import com.edumanage.studentservice.dto.EnrollmentResponse;
import com.edumanage.studentservice.event.StudentEnrolledEvent;
import com.edumanage.studentservice.exception.ResourceNotFoundException;
import com.edumanage.studentservice.mapper.StudentMapper;
import com.edumanage.studentservice.model.Enrollment;
import com.edumanage.studentservice.model.Student;
import com.edumanage.studentservice.repository.EnrollmentRepository;
import com.edumanage.studentservice.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import feign.FeignException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseServiceClient courseServiceClient;
    private final KafkaTemplate<String, StudentEnrolledEvent> kafkaTemplate;
    private final StudentMapper studentMapper;

    private static final String STUDENT_ENROLLED_TOPIC = "student.enrolled";

    @Transactional
    public EnrollmentResponse enroll(UUID studentId, EnrollmentRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        UUID courseId = request.getCourseId();

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalArgumentException("Student is already enrolled in course: " + courseId);
        }

        // Saga step 1: Check eligibility via course-service (sync Feign call)
        EligibilityResponse eligibility = courseServiceClient.checkEligibility(courseId, studentId);
        if (!eligibility.isEligible()) {
            throw new IllegalStateException("Enrollment not allowed: " + eligibility.getReason());
        }

        // Saga step 2: Persist enrollment locally
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .courseId(courseId)
                .build();
        Enrollment saved = enrollmentRepository.save(enrollment);

        // Saga step 3: Increment course counter (sync Feign call)
        // Compensating transaction: if this fails, delete the local enrollment to stay consistent
        try {
            courseServiceClient.incrementEnrollment(courseId);
        } catch (FeignException e) {
            log.error("Failed to increment enrollment counter for courseId={}, rolling back enrollment id={}",
                    courseId, saved.getId(), e);
            enrollmentRepository.delete(saved);
            throw new IllegalStateException("Enrollment failed: could not update course capacity. Please try again.", e);
        }

        // Saga step 4: Publish domain event — fee-service & notification-service react async
        StudentEnrolledEvent event = StudentEnrolledEvent.builder()
                .enrollmentId(saved.getId())
                .studentId(studentId)
                .courseId(courseId)
                .parentId(student.getParentId())
                .studentCode(student.getStudentCode())
                .enrolledAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send(STUDENT_ENROLLED_TOPIC, studentId.toString(), event);
        log.info("Published student.enrolled event for studentId={}, courseId={}", studentId, courseId);

        return studentMapper.toEnrollmentResponse(saved);
    }

    public List<EnrollmentResponse> getEnrollmentsByStudent(UUID studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(studentMapper::toEnrollmentResponse)
                .toList();
    }
}
