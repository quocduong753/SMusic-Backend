package com.example.musicplayer.repository;

import com.example.musicplayer.model.User;
import com.example.musicplayer.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findTopByUserOrderByEndAtDesc(User user);
    boolean existsByUserAndEndAtAfter(User user, LocalDateTime now);
    List<UserSubscription> findByUserIdOrderByStartAtDesc(Long userId);


}
