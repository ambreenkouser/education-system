package com.edumanage.attendanceservice.event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceMarkedEvent {
    private UUID recordId;
    private UUID studentId;
    private UUID courseId;
    private LocalDate attendanceDate;
    private String status;
    private UUID parentId;
}
