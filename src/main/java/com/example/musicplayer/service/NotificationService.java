package com.example.musicplayer.service;

import com.example.musicplayer.dto.request.NotificationRequest;
import com.example.musicplayer.dto.response.NotificationResponse;
import com.example.musicplayer.exception.AppException;
import com.example.musicplayer.exception.ErrorCode;
import com.example.musicplayer.mapper.NotificationMapper;
import com.example.musicplayer.model.Notification;
import com.example.musicplayer.model.User;
import com.example.musicplayer.model.UserDeviceToken;
import com.example.musicplayer.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserDeviceTokenService tokenService;
    private final FirebaseService firebaseService;

    /**
     * Tạo một thông báo mới
     */
    public Notification createNotification(User user, NotificationRequest request) {
        if (user == null || request == null) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        Notification notification = notificationMapper.toNotification(request, user);
        notification.setCreatedAt(LocalDateTime.now());
        Notification saveNotification =notificationRepository.save(notification);
        List<UserDeviceToken> tokens = tokenService.getActiveTokensByUser(user);
        firebaseService.sendPushToUser(
                user,
                request.getTitle(),
                request.getMessage(),
                request.getTargetType(),
                request.getTargetId(),
                tokens,
                saveNotification.getId()
        );
        return saveNotification;
    }

    /**
     * Lấy danh sách thông báo của người dùng và đánh dấu tất cả là đã đọc
     */
    @Transactional
    public List<NotificationResponse> getUserNotifications(User user, Pageable pageable) {
        if (user == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Page<Notification> page = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        List<Notification> unread = page.getContent().stream()
                .filter(n -> !n.isRead())
                .toList();

        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);

        return page.getContent().stream()
                .map(notificationMapper::toNotificationResponse)
                .toList();
    }

    @Transactional
    public List<NotificationResponse> getAllUserNotifications(User user) {
        if (user == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        List<Notification> notifications = notificationRepository
                .findByUserOrderByCreatedAtDesc(user, Pageable.unpaged())
                .getContent();

        // Đánh dấu các thông báo chưa đọc là đã đọc
        List<Notification> unread = notifications.stream()
                .filter(n -> !n.isRead())
                .toList();

        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);

        return notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .toList();
    }

    /**
     * Đánh dấu một thông báo cụ thể là đã đọc
     */
    @Transactional
    public void markAsRead(Long notificationId, User currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    /**
     * Đánh dấu toàn bộ thông báo là đã đọc
     */
    @Transactional
    public void markAllAsRead(User user) {
        if (user == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        List<Notification> unread = notificationRepository.findByUserOrderByCreatedAtDesc(user, Pageable.unpaged())
                .stream()
                .filter(n -> !n.isRead())
                .toList();

        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }


    public long countUnreadNotifications(User user) {
        if (user == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return notificationRepository.countByUserAndReadFalse(user);
    }

}