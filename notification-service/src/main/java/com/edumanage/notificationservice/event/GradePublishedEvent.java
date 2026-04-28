package com.edumanage.notificationservice.event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GradePublishedEvent {
    private UUID gradeRecordId;
    private UUID studentId;
    private UUID examId;
    private String examTitle;
    private Double marksObtained;
    private String gradeLetter;
}
