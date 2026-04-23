package com.edumanage.studentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibilityResponse {
    private UUID courseId;
    private UUID studentId;
    private boolean eligible;
    private String reason;
}
