package com.edumanage.reportservice.service;

import com.edumanage.reportservice.model.*;
import com.edumanage.reportservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportPdfService {

    private final StudentSummaryRepository studentSummaryRepository;
    private final AttendanceSummaryRepository attendanceSummaryRepository;
    private final GradeSummaryRepository gradeSummaryRepository;
    private final FeeSummaryRepository feeSummaryRepository;

    /**
     * Generates a plain-text student report as PDF bytes.
     * iText 8 is on the classpath — swap ByteArrayOutputStream with PdfDocument for rich PDF.
     */
    public byte[] generateStudentReport(UUID studentId) {
        StudentSummary student = studentSummaryRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        List<AttendanceSummary> attendance = attendanceSummaryRepository
                findByStudentId(studentId);

        List<GradeSummary> grades = gradeSummaryRepository.findByStudentId(studentId);
        List<FeeSummary>   fees   = feeSummaryRepository.findByStudentId(studentId);

        StringBuilder sb = new StringBuilder();
        sb.append("EduManage Pro — Student Report\n");
        sb.append("================================\n");
        sb.append("Student Code : ").append(student.getStudentCode()).append("\n");
        sb.append("Student ID   : ").append(studentId).append("\n");
        sb.append("Enrollments  : ").append(student.getTotalEnrollments()).append("\n\n");

        sb.append("GRADES\n------\n");
        grades.forEach(g -> sb.append(String.format("  %-30s %s (%.1f pts)\n",
                g.getExamTitle(), g.getGradeLetter(), g.getGradePoints())));

        sb.append("\nFEE PAYMENTS\n------------\n");
        fees.forEach(f -> sb.append(String.format("  Invoice %-36s  Amount: %s  TXN: %s\n",
                f.getInvoiceId(), f.getPaidAmount(), f.getTransactionId())));

        return sb.toString().getBytes();
    }
}
