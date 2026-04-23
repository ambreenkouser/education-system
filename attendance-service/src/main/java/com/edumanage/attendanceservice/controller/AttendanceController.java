package com.edumanage.attendanceservice.controller;

import com.edumanage.attendanceservice.dto.AttendanceRequest;
import com.edumanage.attendanceservice.dto.AttendanceResponse;
import com.edumanage.attendanceservice.dto.AttendanceSummary;
import com.edumanage.attendanceservice.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Attendance tracking and reporting")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/mark")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Mark attendance for a student")
    public AttendanceResponse mark(
            @Valid @RequestBody AttendanceRequest request,
            @RequestHeader("X-User-Id") UUID markedBy) {
        return attendanceService.mark(request, markedBy);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all attendance records for a student")
    public List<AttendanceResponse> getByStudent(@PathVariable UUID studentId) {
        return attendanceService.getByStudent(studentId);
    }

    @GetMapping("/course/{courseId}/date/{date}")
    @Operation(summary = "Get attendance for a course on a specific date (cached)")
    public List<AttendanceResponse> getByCourseAndDate(
            @PathVariable UUID courseId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceService.getByCourseAndDate(courseId, date);
    }

    @GetMapping("/student/{studentId}/course/{courseId}/summary")
    @Operation(summary = "Get attendance summary with percentage")
    public AttendanceSummary getSummary(
            @PathVariable UUID studentId,
            @PathVariable UUID courseId) {
        return attendanceService.getSummary(studentId, courseId);
    }
}
