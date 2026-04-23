package com.edumanage.attendanceservice.mapper;

import com.edumanage.attendanceservice.dto.AttendanceResponse;
import com.edumanage.attendanceservice.model.AttendanceRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    @Mapping(target = "status", expression = "java(record.getStatus().name())")
    AttendanceResponse toResponse(AttendanceRecord record);
}
