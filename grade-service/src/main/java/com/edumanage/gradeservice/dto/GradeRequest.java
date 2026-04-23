package com.edumanage.gradeservice.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class GradeRequest {
    @NotNull private UUID studentId;
    @NotNull private UUID examId;
    @NotNull @DecimalMin("0.0") @DecimalMax("1000.0") private Double marksObtained;
}
