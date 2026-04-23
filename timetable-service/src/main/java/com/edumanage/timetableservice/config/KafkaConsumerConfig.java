package com.edumanage.timetableservice.config;

import com.edumanage.timetableservice.event.StudentEnrolledEvent;
import com.edumanage.timetableservice.service.TimetableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final TimetableService timetableService;

    @KafkaListener(
            topics = "student.enrolled",
            groupId = "timetable-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onStudentEnrolled(StudentEnrolledEvent event) {
        log.info("Received student.enrolled: studentId={}, courseId={}",
                event.getStudentId(), event.getCourseId());
        timetableService.handleStudentEnrolled(event.getCourseId(), event.getStudentId());
    }
}
