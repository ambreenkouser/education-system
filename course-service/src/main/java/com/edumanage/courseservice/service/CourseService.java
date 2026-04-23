package com.edumanage.courseservice.service;

import com.edumanage.courseservice.dto.CourseRequest;
import com.edumanage.courseservice.dto.CourseResponse;
import com.edumanage.courseservice.dto.EligibilityResponse;
import com.edumanage.courseservice.exception.ResourceNotFoundException;
import com.edumanage.courseservice.mapper.CourseMapper;
import com.edumanage.courseservice.model.Course;
import com.edumanage.courseservice.model.CourseStatus;
import com.edumanage.courseservice.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Transactional
    public CourseResponse create(CourseRequest request) {
        if (courseRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Course code already exists: " + request.getCode());
        }
        Course saved = courseRepository.save(courseMapper.toEntity(request));
        return courseMapper.toResponse(saved);
    }

    public CourseResponse findById(UUID id) {
        return courseMapper.toResponse(getCourseOrThrow(id));
    }

    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    public List<CourseResponse> findAvailable() {
        return courseRepository.findAvailableCourses().stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    @Transactional
    public CourseResponse update(UUID id, CourseRequest request) {
        Course course = getCourseOrThrow(id);
        courseMapper.updateFromRequest(request, course);
        return courseMapper.toResponse(courseRepository.save(course));
    }

    @Transactional
    public void deactivate(UUID id) {
        Course course = getCourseOrThrow(id);
        course.setStatus(CourseStatus.INACTIVE);
        courseRepository.save(course);
    }

    public EligibilityResponse checkEligibility(UUID courseId, UUID studentId) {
        Course course = getCourseOrThrow(courseId);

        if (course.getStatus() != CourseStatus.ACTIVE) {
            return EligibilityResponse.builder()
                    .courseId(courseId).studentId(studentId)
                    .eligible(false).reason("Course is not active").build();
        }
        if (course.getEnrolledCount() >= course.getMaxStudents()) {
            return EligibilityResponse.builder()
                    .courseId(courseId).studentId(studentId)
                    .eligible(false).reason("Course is at full capacity").build();
        }
        return EligibilityResponse.builder()
                .courseId(courseId).studentId(studentId)
                .eligible(true).reason("Eligible for enrollment").build();
    }

    @Transactional
    public void incrementEnrolledCount(UUID courseId) {
        Course course = getCourseOrThrow(courseId);
        if (course.getEnrolledCount() >= course.getMaxStudents()) {
            throw new IllegalStateException("Course " + courseId + " is already full");
        }
        course.setEnrolledCount(course.getEnrolledCount() + 1);
        courseRepository.save(course);
    }

    private Course getCourseOrThrow(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));
    }
}
