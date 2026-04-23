package com.edumanage.studentservice.mapper;

import com.edumanage.studentservice.dto.EnrollmentResponse;
import com.edumanage.studentservice.dto.StudentResponse;
import com.edumanage.studentservice.model.Enrollment;
import com.edumanage.studentservice.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "status", expression = "java(student.getStatus().name())")
    StudentResponse toResponse(Student student);

    @Mapping(target = "studentId", expression = "java(enrollment.getStudent().getId())")
    @Mapping(target = "status", expression = "java(enrollment.getStatus().name())")
    EnrollmentResponse toEnrollmentResponse(Enrollment enrollment);
}
