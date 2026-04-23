package com.edumanage.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String phone;
    private String address;
    private String avatarUrl;
}
