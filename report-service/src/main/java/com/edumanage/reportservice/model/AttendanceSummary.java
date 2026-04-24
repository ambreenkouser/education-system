package com.edumanage.reportservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_attendance_summary")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AttendanceSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID studentId;
    private UUID courseId;
    private int totalClasses;
    private int presentCount;
    private int absentCount;
    private int lateCount;
    private LocalDateTime lastUpdated;

    public double getAttendancePercentage() {
        if (totalClasses == 0) return 0.0;
        return (double) (presentCount + lateCount) / totalClasses * 100.0;
    }
}
