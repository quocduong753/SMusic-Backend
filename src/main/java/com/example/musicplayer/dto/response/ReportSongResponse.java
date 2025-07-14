package com.example.musicplayer.dto.response;

import com.example.musicplayer.enums.ReportStatus;
import com.example.musicplayer.enums.ViolationReason;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportSongResponse {
    private Long id;
    private SongResponse song;
    private UserResponse reporter;  // null nếu là AI
    private ViolationReason reason;
    private String description;
    private boolean isByAI;
    private LocalDateTime reportedAt;
    private ReportStatus status;
}
