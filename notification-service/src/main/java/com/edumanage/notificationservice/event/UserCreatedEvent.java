package com.edumanage.notificationservice.event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserCreatedEvent {
    private UUID userId;
    private String email;
    private String role;
}
