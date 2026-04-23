package com.edumanage.attendanceservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AttendanceResponse {
    private UUID id;
    private UUID studentId;
    private UUID courseId;
    private LocalDate attendanceDate;
    private String status;
    private UUID markedBy;
    private String remarks;
    private LocalDateTime createdAt;
}
