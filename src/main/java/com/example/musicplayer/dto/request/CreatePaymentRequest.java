package com.example.musicplayer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {
    private Long planId;
    private String provider; // Ví dụ: MOMO, STRIPE
}
