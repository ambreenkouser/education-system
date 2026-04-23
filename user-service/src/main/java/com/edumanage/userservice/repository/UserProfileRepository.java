package com.edumanage.userservice.repository;

import com.edumanage.userservice.model.UserProfile;
import com.edumanage.userservice.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUserId(UUID userId);
    Optional<UserProfile> findByEmail(String email);
    List<UserProfile> findByUserType(UserType userType);
}
