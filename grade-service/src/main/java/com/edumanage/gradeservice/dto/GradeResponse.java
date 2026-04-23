package com.edumanage.gradeservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class GradeResponse {
    private UUID id;
    private UUID studentId;
    private UUID examId;
    private String examTitle;
    private Double marksObtained;
    private Double totalMarks;
    private String gradeLetter;
    private Double gradePoints;
    private LocalDateTime gradedAt;
}
