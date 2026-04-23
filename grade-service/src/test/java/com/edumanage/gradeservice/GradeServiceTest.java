package com.edumanage.gradeservice;

import com.edumanage.gradeservice.dto.GradeRequest;
import com.edumanage.gradeservice.mapper.GradeMapper;
import com.edumanage.gradeservice.model.Exam;
import com.edumanage.gradeservice.model.ExamType;
import com.edumanage.gradeservice.model.GradeRecord;
import com.edumanage.gradeservice.outbox.OutboxEventRepository;
import com.edumanage.gradeservice.repository.ExamRepository;
import com.edumanage.gradeservice.repository.GradeRecordRepository;
import com.edumanage.gradeservice.service.GradeCalculator;
import com.edumanage.gradeservice.service.GradeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock GradeRecordRepository gradeRecordRepository;
    @Mock ExamRepository examRepository;
    @Mock OutboxEventRepository outboxEventRepository;
    @Mock GradeMapper gradeMapper;
    @Mock ObjectMapper objectMapper;
    @InjectMocks GradeService gradeService;

    private final GradeCalculator gradeCalculator = new GradeCalculator();

    @Test
    void submitGrade_whenAlreadyExists_throwsIllegalArgument() {
        UUID studentId = UUID.randomUUID();
        UUID examId    = UUID.randomUUID();

        GradeRequest req = new GradeRequest();
        req.setStudentId(studentId);
        req.setExamId(examId);
        req.setMarksObtained(80.0);

        when(gradeRecordRepository.findByStudentIdAndExamId(studentId, examId))
                .thenReturn(Optional.of(GradeRecord.builder().build()));

        assertThatThrownBy(() -> gradeService.submitGrade(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already submitted");
    }

    @Test
    void gradeCalculator_correctLetterForPercentage() {
        // 80/100 = 80% → A-
        assertThat(gradeCalculator.calculateGradeLetter(80.0)).isEqualTo("A-");
        assertThat(gradeCalculator.calculateGradePoints("A-")).isEqualTo(3.7);
    }
}
