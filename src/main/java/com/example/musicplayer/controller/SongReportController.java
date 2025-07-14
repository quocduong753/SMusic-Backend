package com.example.musicplayer.controller;


import com.example.musicplayer.dto.common.ApiResponse;
import com.example.musicplayer.dto.request.ReportSongRequest;
import com.example.musicplayer.dto.response.ReportSongResponse;
import com.example.musicplayer.model.User;
import com.example.musicplayer.service.SongReportService;
import com.example.musicplayer.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongReportController {

    private final SongReportService songReportService;

    @PostMapping("/{id}/report")
    public ResponseEntity<ApiResponse<ReportSongResponse>> reportSong(
            @PathVariable Long id,
            @RequestBody ReportSongRequest request) {

        User currentUser = SecurityUtil.getCurrentUser();
        ReportSongResponse response = songReportService.reportSong(currentUser, id, request);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
}
