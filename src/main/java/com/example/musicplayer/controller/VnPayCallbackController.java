package com.example.musicplayer.controller;

import com.example.musicplayer.config.VNPayConfig;
import com.example.musicplayer.exception.AppException;
import com.example.musicplayer.exception.ErrorCode;
import com.example.musicplayer.model.PaymentTransaction;
import com.example.musicplayer.model.User;
import com.example.musicplayer.repository.PaymentTransactionRepository;
import com.example.musicplayer.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/payment/vnpay-return")
@RequiredArgsConstructor
public class VnPayCallbackController {

    private final VNPayConfig config;
    private final PaymentTransactionService transactionService;
    private final PaymentTransactionRepository transactionRepository;

    @GetMapping
    public ResponseEntity<String> handleVnPayReturn(@RequestParam Map<String, String> params) {
        // 1. Lấy secure hash và loại khỏi params
        String vnp_SecureHash = params.get("vnp_SecureHash");
        Map<String, String> filteredParams = new HashMap<>(params);
        filteredParams.remove("vnp_SecureHash");

        // 2. Tạo chuỗi ký lại
        List<String> fieldNames = new ArrayList<>(filteredParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (Iterator<String> it = fieldNames.iterator(); it.hasNext();) {
            String key = it.next();
            String value = filteredParams.get(key);
            hashData.append(key).append('=')
                    .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            if (it.hasNext()) hashData.append('&');
        }

        // 3. Xác thực chữ ký
        String expectedHash = VNPayConfig.hmacSHA512(config.getSecretKey(), hashData.toString());
        if (!expectedHash.equals(vnp_SecureHash)) {
            return ResponseEntity.badRequest().body("Chữ ký không hợp lệ. Giao dịch bị từ chối.");
        }

        // 4. Kiểm tra mã kết quả từ VNPAY
        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");
        Long transactionId = Long.parseLong(txnRef);

        // 5. Truy xuất giao dịch và user
        PaymentTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        User user = transaction.getUser();

        // 6. Nếu thành công thì xác nhận
        if ("00".equals(responseCode)) {
            transactionService.confirmTransaction(transactionId, user);
        }

        // 7. Tạo redirect URL với đầy đủ thông tin
        String planName = URLEncoder.encode(transaction.getPlan().getName(), StandardCharsets.UTF_8);
        String amount = transaction.getAmount().toPlainString();
        String createdAt = URLEncoder.encode(
                (transaction.getConfirmedAt() != null ? transaction.getConfirmedAt() : transaction.getCreatedAt()).toString(),
                StandardCharsets.UTF_8
        );

        String redirectUrl = "myapp://payment-result"
                + "?vnp_TxnRef=" + txnRef
                + "&success=" + ("00".equals(responseCode) ? "true" : "false")
                + "&planName=" + planName
                + "&amount=" + amount
                + "&createdAt=" + createdAt;

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}
