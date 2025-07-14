package com.example.musicplayer.dto.request;

import com.example.musicplayer.enums.NotificationTargetType;
import com.example.musicplayer.enums.NotificationType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private String title;
    private String message;
    private NotificationType type;
    private NotificationTargetType targetType;
    private Long targetId;
}