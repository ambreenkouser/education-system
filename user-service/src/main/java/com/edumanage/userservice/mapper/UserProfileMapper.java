package com.edumanage.userservice.mapper;

import com.edumanage.userservice.dto.UserProfileRequest;
import com.edumanage.userservice.dto.UserProfileResponse;
import com.edumanage.userservice.model.UserProfile;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "userType", expression = "java(profile.getUserType().name())")
    UserProfileResponse toResponse(UserProfile profile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(UserProfileRequest request, @MappingTarget UserProfile profile);
}
