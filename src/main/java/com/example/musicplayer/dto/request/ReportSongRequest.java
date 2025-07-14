package com.example.musicplayer.dto.request;

import com.example.musicplayer.enums.ViolationReason;
import lombok.Data;

@Data
public class ReportSongRequest {
    private ViolationReason reason;
    private String description;
}
