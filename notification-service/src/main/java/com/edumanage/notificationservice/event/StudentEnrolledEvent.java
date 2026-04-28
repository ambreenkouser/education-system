package com.edumanage.notificationservice.event;
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
    private UUID parentId;
    private String studentCode;\n    private String studentEmail;
    private LocalDateTime enrolledAt;
}
