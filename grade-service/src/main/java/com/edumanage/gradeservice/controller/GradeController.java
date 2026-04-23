package com.edumanage.gradeservice.controller;

import com.edumanage.gradeservice.dto.ExamRequest;
import com.edumanage.gradeservice.dto.GradeRequest;
import com.edumanage.gradeservice.dto.GradeResponse;
import com.edumanage.gradeservice.model.Exam;
import com.edumanage.gradeservice.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@Tag(name = "Grades", description = "Exam and grade management")
public class GradeController {

    private final GradeService gradeService;

    @PostMapping("/exams")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new exam")
    public Exam createExam(@Valid @RequestBody ExamRequest request) {
        return gradeService.createExam(request);
    }

    @GetMapping("/exams/course/{courseId}")
    @Operation(summary = "Get all exams for a course")
    public List<Exam> getExamsByCourse(@PathVariable UUID courseId) {
        return gradeService.getExamsByCourse(courseId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit a grade (triggers Outbox → grade.published event)")
    public GradeResponse submitGrade(@Valid @RequestBody GradeRequest request) {
        return gradeService.submitGrade(request);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all grades for a student")
    public List<GradeResponse> getByStudent(@PathVariable UUID studentId) {
        return gradeService.getByStudent(studentId);
    }

    @GetMapping("/student/{studentId}/gpa")
    @Operation(summary = "Calculate GPA for a student (4.0 scale)")
    public Map<String, Double> getGpa(@PathVariable UUID studentId) {
        return Map.of("gpa", gradeService.getGpa(studentId));
    }
}
