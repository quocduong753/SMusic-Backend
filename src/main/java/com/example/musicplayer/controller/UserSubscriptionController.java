package com.example.musicplayer.controller;

import com.example.musicplayer.dto.common.ApiResponse;
import com.example.musicplayer.dto.response.UserSubscriptionResponse;
import com.example.musicplayer.model.User;
import com.example.musicplayer.service.UserSubscriptionService;
import com.example.musicplayer.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user-subscriptions")
@RequiredArgsConstructor
public class UserSubscriptionController {

    private final UserSubscriptionService service;

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<UserSubscriptionResponse>>> getHistory(
    ) {
        User currentUser = SecurityUtil.getCurrentUser();
        List<UserSubscriptionResponse> history = service.getUserHistory(currentUser.getId());
        return ResponseEntity.ok(new ApiResponse<>(history));
    }
}
