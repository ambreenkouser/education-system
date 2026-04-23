package com.edumanage.userservice.config;

import com.edumanage.userservice.event.UserCreatedEvent;
import com.edumanage.userservice.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final UserProfileService userProfileService;

    @KafkaListener(
            topics = "user.created",
            groupId = "user-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onUserCreated(UserCreatedEvent event) {
        log.info("Received user.created event for userId={}", event.getUserId());
        userProfileService.createFromEvent(event);
    }
}
