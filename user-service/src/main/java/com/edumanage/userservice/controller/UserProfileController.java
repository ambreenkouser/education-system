package com.edumanage.userservice.controller;

import com.edumanage.userservice.dto.UserProfileRequest;
import com.edumanage.userservice.dto.UserProfileResponse;
import com.edumanage.userservice.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management endpoints")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    @Operation(summary = "Get own profile using X-User-Id header injected by gateway")
    public UserProfileResponse getMyProfile(@RequestHeader("X-User-Id") UUID userId) {
        return userProfileService.findByUserId(userId);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get profile by userId")
    public UserProfileResponse findByUserId(@PathVariable UUID userId) {
        return userProfileService.findByUserId(userId);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user profile")
    public UserProfileResponse update(
            @PathVariable UUID userId,
            @Valid @RequestBody UserProfileRequest request) {
        return userProfileService.update(userId, request);
    }
}
