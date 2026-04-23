package com.edumanage.courseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class EligibilityResponse {
    private UUID courseId;
    private UUID studentId;
    private boolean eligible;
    private String reason;
}
