package com.edumanage.gradeservice.service;

import com.edumanage.gradeservice.dto.ExamRequest;
import com.edumanage.gradeservice.dto.GradeRequest;
import com.edumanage.gradeservice.dto.GradeResponse;
import com.edumanage.gradeservice.event.GradePublishedEvent;
import com.edumanage.gradeservice.exception.ResourceNotFoundException;
import com.edumanage.gradeservice.mapper.GradeMapper;
import com.edumanage.gradeservice.model.Exam;
import com.edumanage.gradeservice.model.GradeRecord;
import com.edumanage.gradeservice.outbox.OutboxEvent;
import com.edumanage.gradeservice.outbox.OutboxEventRepository;
import com.edumanage.gradeservice.repository.ExamRepository;
import com.edumanage.gradeservice.repository.GradeRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRecordRepository gradeRecordRepository;
    private final ExamRepository examRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final GradeCalculator gradeCalculator;
    private final GradeMapper gradeMapper;
    private final ObjectMapper objectMapper;

    private static final String GRADE_PUBLISHED_TOPIC = "grade.published";

    @Transactional
    public Exam createExam(ExamRequest request) {
        return examRepository.save(Exam.builder()
                .courseId(request.getCourseId())
                .title(request.getTitle())
                .examDate(request.getExamDate())
                .totalMarks(request.getTotalMarks())
                .type(request.getType())
                .build());
    }

    @Transactional
    public GradeResponse submitGrade(GradeRequest request) {
        if (gradeRecordRepository.findByStudentIdAndExamId(
                request.getStudentId(), request.getExamId()).isPresent()) {
            throw new IllegalArgumentException("Grade already submitted for student "
                    + request.getStudentId() + " on exam " + request.getExamId());
        }

        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + request.getExamId()));

        double percentage = (request.getMarksObtained() / exam.getTotalMarks()) * 100;
        String gradeLetter = gradeCalculator.calculateGradeLetter(percentage);
        double gradePoints = gradeCalculator.calculateGradePoints(gradeLetter);

        GradeRecord record = GradeRecord.builder()
                .studentId(request.getStudentId())
                .exam(exam)
                .marksObtained(request.getMarksObtained())
                .gradeLetter(gradeLetter)
                .gradePoints(gradePoints)
                .build();

        GradeRecord saved = gradeRecordRepository.save(record);

        // Outbox pattern: write event to outbox table in same transaction
        persistOutboxEvent(saved, exam);

        log.info("Grade submitted: studentId={}, grade={}", request.getStudentId(), gradeLetter);
        return gradeMapper.toResponse(saved);
    }

    public List<GradeResponse> getByStudent(UUID studentId) {
        return gradeRecordRepository.findByStudentId(studentId).stream()
                .map(gradeMapper::toResponse).toList();
    }

    public double getGpa(UUID studentId) {
        return gradeRecordRepository.calculateGpa(studentId).orElse(0.0);
    }

    public List<Exam> getExamsByCourse(UUID courseId) {
        return examRepository.findByCourseId(courseId);
    }

    private void persistOutboxEvent(GradeRecord record, Exam exam) {
        GradePublishedEvent event = GradePublishedEvent.builder()
                .gradeRecordId(record.getId())
                .studentId(record.getStudentId())
                .courseId(exam.getCourseId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .marksObtained(record.getMarksObtained())
                .totalMarks(exam.getTotalMarks())
                .gradeLetter(record.getGradeLetter())
                .gradePoints(record.getGradePoints())
                .gradedAt(LocalDateTime.now())
                .build();

        try {
            outboxEventRepository.save(OutboxEvent.builder()
                    .topic(GRADE_PUBLISHED_TOPIC)
                    .aggregateId(record.getStudentId().toString())
                    .payload(objectMapper.writeValueAsString(event))
                    .build());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize grade event for outbox: {}", e.getMessage());
            throw new RuntimeException("Outbox event serialization failed", e);
        }
    }
}
