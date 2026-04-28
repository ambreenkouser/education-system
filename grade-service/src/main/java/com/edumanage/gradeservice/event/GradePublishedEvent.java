package com.edumanage.gradeservice.event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradePublishedEvent {
    private UUID gradeRecordId;
    private UUID studentId;
    private UUID courseId;
    private UUID examId;
    private String examTitle;
    private Double marksObtained;
    private Double totalMarks;
    private String gradeLetter;
    private Double gradePoints;
    private LocalDateTime gradedAt;
}
