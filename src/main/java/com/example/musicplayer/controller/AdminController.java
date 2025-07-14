package com.example.musicplayer.controller;

import com.example.musicplayer.dto.common.ApiResponse;
import com.example.musicplayer.dto.request.LoginRequest;
import com.example.musicplayer.dto.response.AuthResponse;
import com.example.musicplayer.dto.response.ReportSongResponse;
import com.example.musicplayer.service.SongReportService;
import com.example.musicplayer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(userService.loginAdmin(request)));
    }


}
