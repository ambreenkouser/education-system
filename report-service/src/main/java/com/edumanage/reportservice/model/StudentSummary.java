package com.edumanage.reportservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_student_summary")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentSummary {

    @Id
    @Column(name = "student_id")
    private UUID studentId;

    private String studentCode;
    private int totalEnrollments;
    private LocalDateTime lastUpdated;
}
