package com.edumanage.gradeservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "grade_records",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "exam_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GradeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(nullable = false)
    private Double marksObtained;

    @Column(nullable = false, length = 2)
    private String gradeLetter;

    @Column(nullable = false)
    private Double gradePoints;

    @CreationTimestamp
    private LocalDateTime gradedAt;
}
