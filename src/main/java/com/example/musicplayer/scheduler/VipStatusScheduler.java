package com.example.musicplayer.scheduler;

import com.example.musicplayer.dto.request.NotificationRequest;
import com.example.musicplayer.enums.NotificationTargetType;
import com.example.musicplayer.enums.NotificationType;
import com.example.musicplayer.model.User;
import com.example.musicplayer.model.UserDeviceToken;
import com.example.musicplayer.repository.UserRepository;
import com.example.musicplayer.service.FirebaseService;
import com.example.musicplayer.service.NotificationService;
import com.example.musicplayer.service.UserDeviceTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VipStatusScheduler {

    private final UserRepository userRepository;
    private final NotificationService notificationService;


    /**
     * Cập nhật isVip = false cho những user vừa hết hạn VIP
     * Chạy mỗi ngày lúc 01:00 sáng
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void updateExpiredVipStatus() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterdayStart = now.minusDays(1).toLocalDate().atStartOfDay();
        LocalDateTime yesterdayEnd = now.toLocalDate().atStartOfDay().minusNanos(1);

        List<User> users = userRepository.findUsersWhoseVipJustExpired(yesterdayStart, yesterdayEnd, now);

        for (User user : users) {
            if (user.isVip()) {
                user.setVip(false);

                // Gửi thông báo
                NotificationRequest request = NotificationRequest.builder()
                        .title("Gói VIP của bạn đã hết hạn")
                        .message("Hãy nâng cấp lại để tiếp tục tận hưởng đặc quyền VIP.")
                        .type(NotificationType.VIP_EXPIRY)
                        .targetType(NotificationTargetType.VIP)
                        .targetId(null)
                        .build();

                notificationService.createNotification(user, request);

            }
        }

        userRepository.saveAll(users);
        System.out.printf("[VIP Scheduler] Hạ cấp %d người dùng VIP và gửi thông báo.%n", users.size());
    }

}
