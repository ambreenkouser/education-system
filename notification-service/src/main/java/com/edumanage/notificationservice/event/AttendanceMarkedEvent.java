package com.edumanage.notificationservice.event;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class AttendanceMarkedEvent {
    private UUID attendanceId;
    private UUID studentId;
    private UUID courseId;
    private LocalDate attendanceDate;
    private String status;
    private UUID parentId;
}
