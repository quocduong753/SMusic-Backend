package com.example.musicplayer.controller;

import com.example.musicplayer.dto.common.ApiResponse;
import com.example.musicplayer.dto.request.FcmTokenRequest;
import com.example.musicplayer.exception.AppException;
import com.example.musicplayer.exception.ErrorCode;
import com.example.musicplayer.model.User;
import com.example.musicplayer.service.UserDeviceTokenService;
import com.example.musicplayer.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-device")
@RequiredArgsConstructor
public class UserDeviceTokenController {

    private final UserDeviceTokenService userDeviceTokenService;

    /**
     * Đăng ký hoặc cập nhật FCM token cho user hiện tại
     */
    @PostMapping("/fcm-token")
    public ResponseEntity<ApiResponse<Void>> registerFcmToken(@RequestBody FcmTokenRequest request) {
        User currentUser = SecurityUtil.getCurrentUser();
        userDeviceTokenService.saveOrUpdateToken(currentUser, request);
        return ResponseEntity.ok(new ApiResponse<>());
    }

    /**
     * Vô hiệu hoá FCM token khi logout
     */
    @PostMapping("/logout-token")
    public ResponseEntity<ApiResponse<Void>> logoutFcmToken(@RequestBody FcmTokenRequest request) {
        if (request.getFcmToken() == null || request.getFcmToken().isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        userDeviceTokenService.deactivateToken(request.getFcmToken());
        return ResponseEntity.ok(new ApiResponse<>());
    }
}
