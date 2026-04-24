package com.edumanage.reportservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_grade_summary")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GradeSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID studentId;
    private UUID examId;
    private String examTitle;
    private Double marksObtained;
    private String gradeLetter;
    private Double gradePoints;
    private LocalDateTime gradedAt;
}
