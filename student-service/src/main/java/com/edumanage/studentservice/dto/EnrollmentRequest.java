package com.edumanage.studentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class EnrollmentRequest {

    @NotNull
    private UUID courseId;
}
