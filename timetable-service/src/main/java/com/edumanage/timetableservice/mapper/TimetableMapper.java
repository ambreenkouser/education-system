package com.edumanage.timetableservice.mapper;

import com.edumanage.timetableservice.dto.TimeSlotResponse;
import com.edumanage.timetableservice.model.TimeSlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TimetableMapper {

    @Mapping(target = "roomId",   expression = "java(ts.getRoom().getId())")
    @Mapping(target = "roomName", expression = "java(ts.getRoom().getName())")
    @Mapping(target = "dayOfWeek", expression = "java(ts.getDayOfWeek().name())")
    TimeSlotResponse toResponse(TimeSlot ts);
}
