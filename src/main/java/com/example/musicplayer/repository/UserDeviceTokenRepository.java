package com.example.musicplayer.repository;

import com.example.musicplayer.model.User;
import com.example.musicplayer.model.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {
    Optional<UserDeviceToken> findByFcmToken(String fcmToken);
    List<UserDeviceToken> findByUserAndActiveTrue(User user);
}