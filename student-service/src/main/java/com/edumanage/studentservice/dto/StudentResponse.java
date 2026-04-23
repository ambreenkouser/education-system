package com.edumanage.studentservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class StudentResponse {
    private UUID id;
    private UUID userId;
    private String studentCode;
    private LocalDate dateOfBirth;
    private UUID parentId;
    private String gradeLevel;
    private String status;
    private LocalDateTime enrollmentDate;
}
