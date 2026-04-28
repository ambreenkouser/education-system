package com.edumanage.studentservice.event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentEnrolledEvent {
    private UUID enrollmentId;
    private UUID studentId;
    private UUID courseId;
    private UUID parentId;
    private String studentCode;
    private LocalDateTime enrolledAt;
}
