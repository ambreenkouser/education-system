package com.edumanage.courseservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class CourseRequest {

    @NotBlank
    @Size(max = 20)
    private String code;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Min(1) @Max(10)
    private Integer credits;

    private UUID teacherId;

    @NotNull
    @Min(1)
    private Integer maxStudents;
}
