package com.example.musicplayer.dto.response;

import com.example.musicplayer.enums.NotificationTargetType;
import com.example.musicplayer.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationTargetType targetType;
    private Long targetId;
    private boolean read;
    private LocalDateTime createdAt;
}
