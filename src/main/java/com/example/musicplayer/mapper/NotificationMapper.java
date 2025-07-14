package com.example.musicplayer.mapper;

import com.example.musicplayer.dto.request.NotificationRequest;
import com.example.musicplayer.dto.response.NotificationResponse;
import com.example.musicplayer.model.Notification;
import com.example.musicplayer.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Notification toNotification(NotificationRequest request, User user);

    NotificationResponse toNotificationResponse(Notification notification);
}
