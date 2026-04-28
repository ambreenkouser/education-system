package com.edumanage.reportservice.event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AttendanceMarkedEvent {
    private UUID attendanceId;
    private UUID studentId;
    private UUID courseId;
    private LocalDate attendanceDate;
    private String status;
}
