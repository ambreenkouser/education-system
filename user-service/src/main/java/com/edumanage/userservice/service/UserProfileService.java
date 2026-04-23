package com.edumanage.userservice.service;

import com.edumanage.userservice.dto.UserProfileRequest;
import com.edumanage.userservice.dto.UserProfileResponse;
import com.edumanage.userservice.event.UserCreatedEvent;
import com.edumanage.userservice.exception.ResourceNotFoundException;
import com.edumanage.userservice.mapper.UserProfileMapper;
import com.edumanage.userservice.model.UserProfile;
import com.edumanage.userservice.model.UserType;
import com.edumanage.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Transactional
    public void createFromEvent(UserCreatedEvent event) {
        if (userProfileRepository.findByUserId(event.getUserId()).isPresent()) {
            log.warn("Profile already exists for userId={}", event.getUserId());
            return;
        }
        UserProfile profile = UserProfile.builder()
                .userId(event.getUserId())
                .email(event.getEmail())
                .userType(UserType.valueOf(event.getRole()))
                .build();
        userProfileRepository.save(profile);
        log.info("Created profile for userId={}", event.getUserId());
    }

    public UserProfileResponse findByUserId(UUID userId) {
        return userProfileMapper.toResponse(getProfileByUserIdOrThrow(userId));
    }

    @Transactional
    public UserProfileResponse update(UUID userId, UserProfileRequest request) {
        UserProfile profile = getProfileByUserIdOrThrow(userId);
        userProfileMapper.updateFromRequest(request, profile);
        return userProfileMapper.toResponse(userProfileRepository.save(profile));
    }

    private UserProfile getProfileByUserIdOrThrow(UUID userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));
    }
}
