package com.edumanage.courseservice;

import com.edumanage.courseservice.dto.CourseRequest;
import com.edumanage.courseservice.dto.EligibilityResponse;
import com.edumanage.courseservice.exception.ResourceNotFoundException;
import com.edumanage.courseservice.mapper.CourseMapper;
import com.edumanage.courseservice.model.Course;
import com.edumanage.courseservice.model.CourseStatus;
import com.edumanage.courseservice.repository.CourseRepository;
import com.edumanage.courseservice.service.CourseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock CourseRepository courseRepository;
    @Mock CourseMapper courseMapper;
    @InjectMocks CourseService courseService;

    @Test
    void checkEligibility_whenFull_returnsNotEligible() {
        UUID courseId = UUID.randomUUID();
        Course course = Course.builder()
                .id(courseId).code("CS101").name("Test")
                .credits(3).maxStudents(30).enrolledCount(30)
                .status(CourseStatus.ACTIVE).build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        EligibilityResponse result = courseService.checkEligibility(courseId, UUID.randomUUID());

        assertThat(result.isEligible()).isFalse();
        assertThat(result.getReason()).contains("capacity");
    }

    @Test
    void checkEligibility_whenAvailable_returnsEligible() {
        UUID courseId = UUID.randomUUID();
        Course course = Course.builder()
                .id(courseId).code("CS101").name("Test")
                .credits(3).maxStudents(30).enrolledCount(10)
                .status(CourseStatus.ACTIVE).build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        EligibilityResponse result = courseService.checkEligibility(courseId, UUID.randomUUID());

        assertThat(result.isEligible()).isTrue();
    }

    @Test
    void findById_whenNotExists_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(courseRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_withDuplicateCode_throwsIllegalArgument() {
        CourseRequest req = new CourseRequest();
        req.setCode("CS101");
        when(courseRepository.existsByCode("CS101")).thenReturn(true);

        assertThatThrownBy(() -> courseService.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CS101");
    }
}
