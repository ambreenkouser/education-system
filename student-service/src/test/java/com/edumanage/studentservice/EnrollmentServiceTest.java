package com.edumanage.studentservice;

import com.edumanage.studentservice.client.CourseServiceClient;
import com.edumanage.studentservice.dto.EligibilityResponse;
import com.edumanage.studentservice.dto.EnrollmentRequest;
import com.edumanage.studentservice.event.StudentEnrolledEvent;
import com.edumanage.studentservice.mapper.StudentMapper;
import com.edumanage.studentservice.model.Enrollment;
import com.edumanage.studentservice.model.Student;
import com.edumanage.studentservice.repository.EnrollmentRepository;
import com.edumanage.studentservice.repository.StudentRepository;
import com.edumanage.studentservice.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock StudentRepository studentRepository;
    @Mock EnrollmentRepository enrollmentRepository;
    @Mock CourseServiceClient courseServiceClient;
    @Mock KafkaTemplate<String, StudentEnrolledEvent> kafkaTemplate;
    @Mock StudentMapper studentMapper;

    @InjectMocks EnrollmentService enrollmentService;

    @Test
    void enroll_whenAlreadyEnrolled_throwsIllegalArgument() {
        UUID studentId = UUID.randomUUID();
        UUID courseId  = UUID.randomUUID();
        Student student = Student.builder().id(studentId).studentCode("STU-001").build();

        EnrollmentRequest req = new EnrollmentRequest();
        req.setCourseId(courseId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(true);

        assertThatThrownBy(() -> enrollmentService.enroll(studentId, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already enrolled");
    }

    @Test
    void enroll_whenNotEligible_throwsIllegalState() {
        UUID studentId = UUID.randomUUID();
        UUID courseId  = UUID.randomUUID();
        Student student = Student.builder().id(studentId).studentCode("STU-001").build();

        EnrollmentRequest req = new EnrollmentRequest();
        req.setCourseId(courseId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(false);
        when(courseServiceClient.checkEligibility(courseId, studentId))
                .thenReturn(EligibilityResponse.builder()
                        .courseId(courseId).studentId(studentId)
                        .eligible(false).reason("Course is full").build());

        assertThatThrownBy(() -> enrollmentService.enroll(studentId, req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Course is full");
    }

    @Test
    void enroll_whenEligible_savesAndPublishesEvent() {
        UUID studentId = UUID.randomUUID();
        UUID courseId  = UUID.randomUUID();
        Student student = Student.builder().id(studentId).studentCode("STU-001").build();
        Enrollment saved = Enrollment.builder().id(UUID.randomUUID())
                .student(student).courseId(courseId).build();

        EnrollmentRequest req = new EnrollmentRequest();
        req.setCourseId(courseId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(false);
        when(courseServiceClient.checkEligibility(courseId, studentId))
                .thenReturn(EligibilityResponse.builder()
                        .courseId(courseId).studentId(studentId)
                        .eligible(true).reason("OK").build());
        when(enrollmentRepository.save(any())).thenReturn(saved);

        enrollmentService.enroll(studentId, req);

        verify(courseServiceClient).incrementEnrollment(courseId);
        verify(kafkaTemplate).send(eq("student.enrolled"), eq(studentId.toString()), any(StudentEnrolledEvent.class));
    }
}
