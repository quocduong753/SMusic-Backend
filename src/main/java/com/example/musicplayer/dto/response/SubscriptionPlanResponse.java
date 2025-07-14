package com.example.musicplayer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPlanResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private int durationInDays;
    private String description;
}
