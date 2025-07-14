package com.example.musicplayer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanRequest {
    private String name;
    private BigDecimal price;
    private int durationInDays;
    private String description;
}