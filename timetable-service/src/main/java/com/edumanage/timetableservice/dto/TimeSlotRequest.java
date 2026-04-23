package com.edumanage.timetableservice.dto;

import com.edumanage.timetableservice.model.DayOfWeek;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class TimeSlotRequest {

    @NotNull private UUID courseId;
    @NotNull private UUID teacherId;
    @NotNull private UUID roomId;
    @NotNull private DayOfWeek dayOfWeek;
    @NotNull private LocalTime startTime;
    @NotNull private LocalTime endTime;
}
