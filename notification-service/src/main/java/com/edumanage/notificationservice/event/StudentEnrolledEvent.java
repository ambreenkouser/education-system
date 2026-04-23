package com.edumanage.notificationservice.event;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class StudentEnrolledEvent {
    private UUID enrollmentId;
    private UUID studentId;
    private UUID courseId;
    private UUID parentId;
    private String studentCode;
    private LocalDateTime enrolledAt;
}
