package com.edumanage.reportservice.controller;

import com.edumanage.reportservice.model.*;
import com.edumanage.reportservice.repository.*;
import com.edumanage.reportservice.service.ReportPdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports")
public class ReportController {

    private final StudentSummaryRepository studentSummaryRepository;
    private final AttendanceSummaryRepository attendanceSummaryRepository;
    private final GradeSummaryRepository gradeSummaryRepository;
    private final FeeSummaryRepository feeSummaryRepository;
    private final ReportPdfService reportPdfService;

    @GetMapping("/students/{studentId}/summary")
    @Operation(summary = "Get student summary report")
    public ResponseEntity<StudentSummary> getStudentSummary(@PathVariable UUID studentId) {
        return studentSummaryRepository.findById(studentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/attendance/course/{courseId}")
    @Operation(summary = "Get attendance summary for a course")
    public List<AttendanceSummary> getAttendanceByCourse(@PathVariable UUID courseId) {
        return attendanceSummaryRepository.findByCourseId(courseId);
    }

    @GetMapping("/grades/student/{studentId}")
    @Operation(summary = "Get grade summary for a student")
    public List<GradeSummary> getGradesByStudent(@PathVariable UUID studentId) {
        return gradeSummaryRepository.findByStudentId(studentId);
    }

    @GetMapping("/fees/student/{studentId}")
    @Operation(summary = "Get fee payment history for a student")
    public List<FeeSummary> getFeesByStudent(@PathVariable UUID studentId) {
        return feeSummaryRepository.findByStudentId(studentId);
    }

    @GetMapping("/students/{studentId}/pdf")
    @Operation(summary = "Download student report as PDF")
    public ResponseEntity<byte[]> downloadStudentReport(@PathVariable UUID studentId) {
        byte[] pdf = reportPdfService.generateStudentReport(studentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"student-report-" + studentId + ".txt\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(pdf);
    }
}
