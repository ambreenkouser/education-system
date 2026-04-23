package com.edumanage.studentservice.client;

import com.edumanage.studentservice.dto.EligibilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "course-service", fallback = CourseServiceClientFallback.class)
public interface CourseServiceClient {

    @GetMapping("/api/courses/{courseId}/eligibility")
    EligibilityResponse checkEligibility(
            @PathVariable UUID courseId,
            @RequestParam UUID studentId);

    @PatchMapping("/api/courses/{courseId}/increment-enrollment")
    void incrementEnrollment(@PathVariable UUID courseId);
}
