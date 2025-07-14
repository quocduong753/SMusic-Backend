package com.example.musicplayer.dto.request;

import com.example.musicplayer.enums.ReportStatus;
import lombok.Data;

@Data
public class HandleReportRequest {
    private ReportStatus status;
}
