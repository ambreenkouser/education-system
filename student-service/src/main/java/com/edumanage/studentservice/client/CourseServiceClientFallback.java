package com.edumanage.studentservice.client;

import com.edumanage.studentservice.dto.EligibilityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class CourseServiceClientFallback implements CourseServiceClient {

    @Override
    public EligibilityResponse checkEligibility(UUID courseId, UUID studentId) {
        log.warn("course-service unavailable, returning ineligible fallback for courseId={}", courseId);
        return EligibilityResponse.builder()
                .courseId(courseId).studentId(studentId)
                .eligible(false).reason("Course service is currently unavailable. Please try again.")
                .build();
    }

    @Override
    public void incrementEnrollment(UUID courseId) {
        log.error("Failed to increment enrollment count for courseId={}. course-service is down.", courseId);
    }
}
