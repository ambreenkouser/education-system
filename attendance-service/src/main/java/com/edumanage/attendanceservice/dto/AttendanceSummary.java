package com.edumanage.attendanceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class AttendanceSummary {
    private UUID studentId;
    private UUID courseId;
    private long totalClasses;
    private long presentCount;
    private long absentCount;
    private long lateCount;
    private double attendancePercentage;
}
