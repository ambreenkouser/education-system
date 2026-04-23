package com.edumanage.timetableservice.dto;

import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class TimeSlotResponse {
    private UUID id;
    private UUID courseId;
    private UUID teacherId;
    private UUID roomId;
    private String roomName;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
