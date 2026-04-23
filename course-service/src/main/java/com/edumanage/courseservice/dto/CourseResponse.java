package com.edumanage.courseservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CourseResponse {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private Integer credits;
    private UUID teacherId;
    private Integer maxStudents;
    private Integer enrolledCount;
    private String status;
    private boolean hasCapacity;
    private LocalDateTime createdAt;
}
