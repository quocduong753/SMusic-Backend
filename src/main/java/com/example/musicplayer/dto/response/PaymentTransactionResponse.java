package com.example.musicplayer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransactionResponse {
    private Long id;
    private BigDecimal amount;
    private Long planId;
    private String provider;
    private String status;
    private String createdAt;
}
