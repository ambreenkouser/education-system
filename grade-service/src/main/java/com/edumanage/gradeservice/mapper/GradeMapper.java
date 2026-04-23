package com.edumanage.gradeservice.mapper;

import com.edumanage.gradeservice.dto.GradeResponse;
import com.edumanage.gradeservice.model.GradeRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GradeMapper {

    @Mapping(target = "examId",    expression = "java(gr.getExam().getId())")
    @Mapping(target = "examTitle", expression = "java(gr.getExam().getTitle())")
    @Mapping(target = "totalMarks",expression = "java(gr.getExam().getTotalMarks())")
    GradeResponse toResponse(GradeRecord gr);
}
