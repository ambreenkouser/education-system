package com.edumanage.gradeservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "exams")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID courseId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate examDate;

    @Column(nullable = false)
    private Double totalMarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamType type;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
