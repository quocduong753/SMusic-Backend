package com.example.musicplayer.service;

import com.example.musicplayer.dto.response.UserSubscriptionResponse;
import com.example.musicplayer.mapper.UserSubscriptionMapper;
import com.example.musicplayer.model.User;
import com.example.musicplayer.model.UserSubscription;
import com.example.musicplayer.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserSubscriptionMapper userSubscriptionMapper;

    public boolean isVip(User user) {
        LocalDateTime now = LocalDateTime.now();
        return userSubscriptionRepository.existsByUserAndEndAtAfter(user, now);
    }

    public List<UserSubscriptionResponse> getUserHistory(Long userId) {
        List<UserSubscription> subscriptions = userSubscriptionRepository.findByUserIdOrderByStartAtDesc(userId);
        return userSubscriptionMapper.toResponses(subscriptions);
    }
}
