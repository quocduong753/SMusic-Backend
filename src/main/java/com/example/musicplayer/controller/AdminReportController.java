package com.example.musicplayer.controller;

import com.example.musicplayer.dto.common.ApiResponse;
import com.example.musicplayer.dto.request.HandleReportRequest;
import com.example.musicplayer.dto.response.ReportSongResponse;
import com.example.musicplayer.enums.ReportStatus;
import com.example.musicplayer.service.SongReportService;
import com.example.musicplayer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {
    private final UserService userService;
    private final SongReportService songReportService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportSongResponse>> getReportById(@PathVariable Long id) {
        ReportSongResponse result = songReportService.getReportById(id);
        return ResponseEntity.ok(new ApiResponse<>(result));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportSongResponse>> handleReport(
            @PathVariable Long id,
            @RequestBody HandleReportRequest request
    ) {
        ReportSongResponse result = songReportService.handleReport(id, request);
        return ResponseEntity.ok(new ApiResponse<>(result));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReportSongResponse>>> getAllReports(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ReportSongResponse> result;
        Pageable pageable = PageRequest.of(page, size);

        if (status != null) {
            result = songReportService.getReportsByStatus(status, pageable);
        } else {
            result = songReportService.getAllReports(pageable);
        }

        return ResponseEntity.ok(new ApiResponse<>(result));
    }

}
