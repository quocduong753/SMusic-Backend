package com.example.musicplayer.mapper;

import com.example.musicplayer.dto.response.PaymentTransactionResponse;
import com.example.musicplayer.model.PaymentTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {
    @Mapping(source = "plan.id", target = "planId")
    PaymentTransactionResponse toResponse(PaymentTransaction transaction);
    List<PaymentTransactionResponse> toResponseList(List<PaymentTransaction> transactions);

}
