package com.example.musicplayer.mapper;

import com.example.musicplayer.dto.request.SubscriptionPlanRequest;
import com.example.musicplayer.dto.response.SubscriptionPlanResponse;
import com.example.musicplayer.model.SubscriptionPlan;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionPlanMapper {

    SubscriptionPlanResponse toResponse(SubscriptionPlan plan);
    List<SubscriptionPlanResponse> toResponses(List<SubscriptionPlan> plans);

    SubscriptionPlan toEntity(SubscriptionPlanRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(SubscriptionPlanRequest request, @MappingTarget SubscriptionPlan plan);
}
