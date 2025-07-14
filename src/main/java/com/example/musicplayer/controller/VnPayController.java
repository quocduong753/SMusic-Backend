package com.example.musicplayer.controller;

import com.example.musicplayer.config.VNPayConfig;
import com.example.musicplayer.dto.common.ApiResponse;
import com.example.musicplayer.dto.request.CreatePaymentRequest;
import com.example.musicplayer.model.User;
import com.example.musicplayer.service.VnPayService;
import com.example.musicplayer.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payment/vnpay")
@RequiredArgsConstructor
public class VnPayController {

    private final VnPayService vnPayService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> createPaymentUrl(
            @RequestBody CreatePaymentRequest request,
            HttpServletRequest httpRequest
    ) {
        User currentUser = SecurityUtil.getCurrentUser();
        String clientIp = VNPayConfig.getIpAddress(httpRequest);

        String url = vnPayService.createPaymentUrl(request, currentUser, clientIp);
        return ResponseEntity.ok(new ApiResponse<>(Map.of("url", url)));
    }
}
