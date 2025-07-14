package com.example.musicplayer.repository;

import com.example.musicplayer.model.PasswordResetToken;
import com.example.musicplayer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByUser(User user);
    Optional<PasswordResetToken> findByUserAndOtp(User user, String otp);
}
