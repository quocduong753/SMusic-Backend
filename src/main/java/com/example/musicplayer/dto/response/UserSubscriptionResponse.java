package com.example.musicplayer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSubscriptionResponse {
    private Long id;
    private SubscriptionPlanResponse plan;
    private PaymentTransactionResponse transaction;
    private String startAt;
    private String endAt;
}

