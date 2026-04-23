package com.edumanage.studentservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EnrollmentResponse {
    private UUID id;
    private UUID studentId;
    private UUID courseId;
    private String status;
    private LocalDateTime enrolledAt;
}
