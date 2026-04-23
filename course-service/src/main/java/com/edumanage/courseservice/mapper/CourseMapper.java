package com.edumanage.courseservice.mapper;

import com.edumanage.courseservice.dto.CourseRequest;
import com.edumanage.courseservice.dto.CourseResponse;
import com.edumanage.courseservice.model.Course;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    Course toEntity(CourseRequest request);

    @Mapping(target = "hasCapacity", expression = "java(course.getEnrolledCount() < course.getMaxStudents())")
    @Mapping(target = "status", expression = "java(course.getStatus().name())")
    CourseResponse toResponse(Course course);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(CourseRequest request, @MappingTarget Course course);
}
