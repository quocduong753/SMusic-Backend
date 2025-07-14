package com.example.musicplayer.service;

import com.example.musicplayer.dto.request.FcmTokenRequest;
import com.example.musicplayer.exception.AppException;
import com.example.musicplayer.exception.ErrorCode;
import com.example.musicplayer.model.User;
import com.example.musicplayer.model.UserDeviceToken;
import com.example.musicplayer.repository.UserDeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDeviceTokenService {

    private final UserDeviceTokenRepository tokenRepository;

    /**
     * Đăng ký hoặc cập nhật token cho người dùng hiện tại
     */
    @Transactional
    public void saveOrUpdateToken(User user, FcmTokenRequest request) {
        String fcmToken = request.getFcmToken();

        if (fcmToken == null || fcmToken.isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        Optional<UserDeviceToken> existingToken = tokenRepository.findByFcmToken(fcmToken);

        if (existingToken.isPresent()) {
            UserDeviceToken token = existingToken.get();

            // Nếu token đang thuộc user khác thì chuyển sang user hiện tại
            if (!token.getUser().getId().equals(user.getId())) {
                token.setUser(user);
            }

            token.setDeviceType(request.getDeviceType());
            token.setActive(true);
            token.setLastUpdated(LocalDateTime.now());

            tokenRepository.save(token);
        } else {
            // Token chưa từng tồn tại → tạo mới
            UserDeviceToken token = UserDeviceToken.builder()
                    .user(user)
                    .fcmToken(fcmToken)
                    .deviceType(request.getDeviceType())
                    .active(true)
                    .lastUpdated(LocalDateTime.now())
                    .build();
            tokenRepository.save(token);
        }
    }

    /**
     * Vô hiệu hoá token (dùng khi logout)
     */
    @Transactional
    public void deactivateToken(String fcmToken) {
        tokenRepository.findByFcmToken(fcmToken).ifPresent(token -> {
            token.setActive(false);
            token.setLastUpdated(LocalDateTime.now());
            tokenRepository.save(token);
        });
    }

    /**
     * Lấy tất cả token đang hoạt động của user
     */
    public List<UserDeviceToken> getActiveTokensByUser(User user) {
        return tokenRepository.findByUserAndActiveTrue(user);
    }

/**   yêu cầu xử lý từ frontend
  n   - Gửi token khi login
     - Xoá token khi logout bằng FirebaseMessaging.instance.deleteToken()
 */
}
