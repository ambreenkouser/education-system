package com.edumanage.attendanceservice.dto;

import com.edumanage.attendanceservice.model.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class AttendanceRequest {
    @NotNull private UUID studentId;
    @NotNull private UUID courseId;
    @NotNull private LocalDate attendanceDate;
    @NotNull private AttendanceStatus status;
    private String remarks;
}
