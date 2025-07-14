package com.example.musicplayer.controller;

import com.example.musicplayer.dto.request.CreatePaymentRequest;
import com.example.musicplayer.dto.response.PaymentTransactionResponse;
import com.example.musicplayer.dto.response.UserSubscriptionResponse;
import com.example.musicplayer.model.User;
import com.example.musicplayer.service.PaymentTransactionService;
import com.example.musicplayer.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentTransactionController {

    private final PaymentTransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<PaymentTransactionResponse> createTransaction(
            @RequestBody CreatePaymentRequest request) {

        User currentUser = SecurityUtil.getCurrentUser(); // lấy user từ context
        PaymentTransactionResponse response = transactionService.createTransaction(request, currentUser);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<UserSubscriptionResponse> confirmTransaction(@PathVariable("id") Long transactionId) {
        User currentUser = SecurityUtil.getCurrentUser();
        UserSubscriptionResponse response = transactionService.confirmTransaction(transactionId, currentUser);
        return ResponseEntity.ok(response);
    }
}
