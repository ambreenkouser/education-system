package com.edumanage.userservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserProfileResponse {
    private UUID id;
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String userType;
    private String avatarUrl;
    private LocalDateTime updatedAt;
}
