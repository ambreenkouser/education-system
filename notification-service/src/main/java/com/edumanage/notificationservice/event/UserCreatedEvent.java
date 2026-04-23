package com.edumanage.notificationservice.event;

import lombok.Data;
import java.util.UUID;

@Data
public class UserCreatedEvent {
    private UUID userId;
    private String email;
    private String role;
}
