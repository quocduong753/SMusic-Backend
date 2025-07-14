package com.example.musicplayer.mapper;

import com.example.musicplayer.dto.request.RegisterRequest;
import com.example.musicplayer.dto.request.UpdateUserRequest;
import com.example.musicplayer.dto.response.UserResponse;
import com.example.musicplayer.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    void updateUserFromUserUpdateRequest(UpdateUserRequest updateUserRequest, @MappingTarget User user);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User toUser(RegisterRequest registerRequest);

    UserResponse toUserResponse(User user);

}