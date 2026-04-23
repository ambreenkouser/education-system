package com.edumanage.gradeservice.dto;

import com.edumanage.gradeservice.model.ExamType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ExamRequest {
    @NotNull  private UUID courseId;
    @NotBlank private String title;
    @NotNull  private LocalDate examDate;
    @NotNull @Positive private Double totalMarks;
    @NotNull  private ExamType type;
}
