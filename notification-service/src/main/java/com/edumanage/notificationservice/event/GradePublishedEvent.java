package com.edumanage.notificationservice.event;

import lombok.Data;
import java.util.UUID;

@Data
public class GradePublishedEvent {
    private UUID gradeRecordId;
    private UUID studentId;
    private UUID examId;
    private String examTitle;
    private Double marksObtained;
    private String gradeLetter;
}
