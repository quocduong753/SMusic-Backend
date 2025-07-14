package com.example.musicplayer.service;

import com.example.musicplayer.config.VNPayConfig;
import com.example.musicplayer.dto.request.CreatePaymentRequest;
import com.example.musicplayer.dto.response.PaymentTransactionResponse;
import com.example.musicplayer.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VnPayService {

    private final VNPayConfig config;
    private final PaymentTransactionService transactionService;

    public String createPaymentUrl(CreatePaymentRequest request, User user, String clientIp) {
        // 1. Gọi service tạo giao dịch với provider = VNPAY
        request.setProvider("VNPAY");
        PaymentTransactionResponse transaction = transactionService.createTransaction(request, user);
        String txnRef = String.valueOf(transaction.getId());

        // 2. Chuẩn bị tham số
        BigDecimal amount = transaction.getAmount().multiply(BigDecimal.valueOf(100)); // VNPAY yêu cầu nhân 100
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", config.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount.longValue()));
//        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan goi VIP" + transaction.getPlanId());
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", config.getReturnUrl());
//        vnp_Params.put("vnp_ReturnAppUrl", "myapp://payment-result?vnp_TxnRef=" + txnRef);

        vnp_Params.put("vnp_IpAddr", clientIp);

        // 3. Thời gian tạo + hết hạn
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now = LocalDateTime.now();
        vnp_Params.put("vnp_CreateDate", now.format(formatter));
        vnp_Params.put("vnp_ExpireDate", now.plusMinutes(15).format(formatter));

        // 4. Tạo chuỗi ký
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder query = new StringBuilder();
        StringBuilder hashData = new StringBuilder();

        for (Iterator<String> it = fieldNames.iterator(); it.hasNext();) {
            String name = it.next();
            String value = vnp_Params.get(name);
            String encodedName = URLEncoder.encode(name, StandardCharsets.US_ASCII);
            String encodedValue = URLEncoder.encode(value, StandardCharsets.US_ASCII);

            query.append(encodedName).append('=').append(encodedValue);
            hashData.append(name).append('=').append(encodedValue); // Phải dùng encodedValue ở đây

            if (it.hasNext()) {
                query.append('&');
                hashData.append('&');
            }
        }


        String secureHash = VNPayConfig.hmacSHA512(config.getSecretKey(), hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        return config.getPayUrl() + "?" + query;
    }
}
