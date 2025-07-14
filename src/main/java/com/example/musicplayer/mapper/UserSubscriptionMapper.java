package com.example.musicplayer.mapper;

import com.example.musicplayer.dto.response.UserSubscriptionResponse;
import com.example.musicplayer.model.UserSubscription;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        SubscriptionPlanMapper.class,
        PaymentTransactionMapper.class
})
public interface UserSubscriptionMapper {

    UserSubscriptionResponse toResponse(UserSubscription subscription);

    List<UserSubscriptionResponse> toResponses(List<UserSubscription> subscriptions);

}
