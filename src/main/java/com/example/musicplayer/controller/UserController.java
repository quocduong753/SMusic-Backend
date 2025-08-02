package com.example.musicplayer.controller;

import com.example.musicplayer.dto.common.ApiResponse;
import com.example.musicplayer.dto.request.*;
import com.example.musicplayer.dto.response.AuthResponse;
import com.example.musicplayer.dto.response.UserResponse;
import com.example.musicplayer.model.User;
import com.example.musicplayer.service.UserDeviceTokenService;
import com.example.musicplayer.service.UserService;
import com.example.musicplayer.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDeviceTokenService deviceTokenService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = userService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateUserRequest request) {

        User currentUser = SecurityUtil.getCurrentUser();
        UserResponse response = userService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        User currentUser = SecurityUtil.getCurrentUser();
        UserResponse response = userService.getUserInfo(currentUser);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @PutMapping("/changePassword")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        User currentUser = SecurityUtil.getCurrentUser();
        userService.changePassword(currentUser, request);
        return ResponseEntity.ok(new ApiResponse<>("Password change success"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.sendOtpToEmail(request.getEmail());
        return ResponseEntity.ok(new ApiResponse<>("OTP đã được gửi đến email của bạn."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPasswordWithOtp(request.getEmail(), request.getOtp(), request.getNewPassword());
        return ResponseEntity.ok(new ApiResponse<>("Đổi mật khẩu thành công."));
    }


}
