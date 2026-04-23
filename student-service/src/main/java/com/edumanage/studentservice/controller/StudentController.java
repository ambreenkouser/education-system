package com.edumanage.studentservice.controller;

import com.edumanage.studentservice.dto.*;
import com.edumanage.studentservice.service.EnrollmentService;
import com.edumanage.studentservice.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student management and enrollment endpoints")
public class StudentController {

    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new student")
    public StudentResponse create(@Valid @RequestBody StudentRequest request) {
        return studentService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    public StudentResponse findById(@PathVariable UUID id) {
        return studentService.findById(id);
    }

    @GetMapping
    @Operation(summary = "List all students (optionally filter by gradeLevel)")
    public List<StudentResponse> findAll(
            @RequestParam(required = false) String gradeLevel) {
        return gradeLevel != null
                ? studentService.findByGradeLevel(gradeLevel)
                : studentService.findAll();
    }

    @PostMapping("/{id}/enroll")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Enroll a student in a course (triggers Saga)")
    public EnrollmentResponse enroll(
            @PathVariable UUID id,
            @Valid @RequestBody EnrollmentRequest request) {
        return enrollmentService.enroll(id, request);
    }

    @GetMapping("/{id}/enrollments")
    @Operation(summary = "Get all enrollments for a student")
    public List<EnrollmentResponse> getEnrollments(@PathVariable UUID id) {
        return enrollmentService.getEnrollmentsByStudent(id);
    }
}
