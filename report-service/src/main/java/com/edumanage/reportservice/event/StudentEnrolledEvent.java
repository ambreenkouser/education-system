package com.edumanage.reportservice.event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class StudentEnrolledEvent {
    private UUID enrollmentId;
    private UUID studentId;
    private UUID courseId;
    private String studentCode;
    private LocalDateTime enrolledAt;
}
