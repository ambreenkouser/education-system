package com.edumanage.notificationservice;

import com.edumanage.notificationservice.consumer.NotificationConsumer;
import com.edumanage.notificationservice.event.AttendanceMarkedEvent;
import com.edumanage.notificationservice.event.UserCreatedEvent;
import com.edumanage.notificationservice.service.DeduplicationService;
import com.edumanage.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock EmailService emailService;
    @Mock DeduplicationService deduplicationService;
    @InjectMocks NotificationConsumer notificationConsumer;

    @Test
    void onUserCreated_sendsWelcomeEmail() {
        UserCreatedEvent event = new UserCreatedEvent();
        event.setUserId(UUID.randomUUID());
        event.setEmail("student@test.com");
        event.setRole("STUDENT");

        when(deduplicationService.isFirstOccurrence(anyString())).thenReturn(true);

        notificationConsumer.onUserCreated(event);

        verify(emailService).sendEmail(eq("student@test.com"), contains("Welcome"), anyString());
    }

    @Test
    void onUserCreated_duplicateEvent_skipsEmail() {
        UserCreatedEvent event = new UserCreatedEvent();
        event.setUserId(UUID.randomUUID());
        event.setEmail("student@test.com");
        event.setRole("STUDENT");

        when(deduplicationService.isFirstOccurrence(anyString())).thenReturn(false);

        notificationConsumer.onUserCreated(event);

        verifyNoInteractions(emailService);
    }

    @Test
    void onAttendanceMarked_absentStatus_sendsParentAlert() {
        AttendanceMarkedEvent event = new AttendanceMarkedEvent();
        event.setAttendanceId(UUID.randomUUID());
        event.setStudentId(UUID.randomUUID());
        event.setCourseId(UUID.randomUUID());
        event.setParentId(UUID.randomUUID());
        event.setAttendanceDate(LocalDate.now());
        event.setStatus("ABSENT");

        when(deduplicationService.isFirstOccurrence(anyString())).thenReturn(true);

        notificationConsumer.onAttendanceMarked(event);

        verify(emailService).sendEmail(anyString(), contains("absent"), anyString());
    }

    @Test
    void onAttendanceMarked_presentStatus_skipsEmail() {
        AttendanceMarkedEvent event = new AttendanceMarkedEvent();
        event.setAttendanceId(UUID.randomUUID());
        event.setStudentId(UUID.randomUUID());
        event.setStatus("PRESENT");

        notificationConsumer.onAttendanceMarked(event);

        verifyNoInteractions(emailService);
        verifyNoInteractions(deduplicationService);
    }
}
