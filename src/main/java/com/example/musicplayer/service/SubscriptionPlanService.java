package com.example.musicplayer.service;

import com.example.musicplayer.dto.request.SubscriptionPlanRequest;
import com.example.musicplayer.dto.response.SubscriptionPlanResponse;
import com.example.musicplayer.exception.AppException;
import com.example.musicplayer.exception.ErrorCode;
import com.example.musicplayer.mapper.SubscriptionPlanMapper;
import com.example.musicplayer.model.SubscriptionPlan;
import com.example.musicplayer.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionPlanMapper planMapper;

    public List<SubscriptionPlanResponse> getAllPlans() {
        return planMapper.toResponses(planRepository.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public SubscriptionPlanResponse createPlan(SubscriptionPlanRequest request) {
        SubscriptionPlan plan = planMapper.toEntity(request);
        planRepository.save(plan);
        return planMapper.toResponse(plan);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public SubscriptionPlanResponse updatePlan(Long id, SubscriptionPlanRequest request) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        planMapper.updateEntityFromRequest(request, plan);
        planRepository.save(plan);
        return planMapper.toResponse(plan);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deletePlan(Long id) {
        if (!planRepository.existsById(id)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        planRepository.deleteById(id);
    }
}
