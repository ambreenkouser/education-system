package com.edumanage.studentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class StudentRequest {

    @NotNull
    private UUID userId;

    @NotBlank
    private String gradeLevel;

    private LocalDate dateOfBirth;
    private UUID parentId;
}
