package com.edumanage.courseservice.controller;

import com.edumanage.courseservice.dto.CourseRequest;
import com.edumanage.courseservice.dto.CourseResponse;
import com.edumanage.courseservice.dto.EligibilityResponse;
import com.edumanage.courseservice.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management endpoints")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new course")
    public CourseResponse create(@Valid @RequestBody CourseRequest request) {
        return courseService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    public CourseResponse findById(@PathVariable UUID id) {
        return courseService.findById(id);
    }

    @GetMapping
    @Operation(summary = "List all courses")
    public List<CourseResponse> findAll() {
        return courseService.findAll();
    }

    @GetMapping("/available")
    @Operation(summary = "List courses with available seats")
    public List<CourseResponse> findAvailable() {
        return courseService.findAvailable();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a course")
    public CourseResponse update(@PathVariable UUID id, @Valid @RequestBody CourseRequest request) {
        return courseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate a course")
    public void deactivate(@PathVariable UUID id) {
        courseService.deactivate(id);
    }

    @GetMapping("/{id}/eligibility")
    @Operation(summary = "Check if a student is eligible to enroll in a course")
    public EligibilityResponse checkEligibility(
            @PathVariable UUID id,
            @RequestParam UUID studentId) {
        return courseService.checkEligibility(id, studentId);
    }

    @PatchMapping("/{id}/increment-enrollment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Increment enrolled count (called by student-service after enrollment)")
    public void incrementEnrollment(@PathVariable UUID id) {
        courseService.incrementEnrolledCount(id);
    }
}
