package com.example.musicplayer.controller;

import com.example.musicplayer.dto.common.ApiResponse;
import com.example.musicplayer.dto.response.NotificationResponse;
import com.example.musicplayer.mapper.NotificationMapper;
import com.example.musicplayer.model.User;
import com.example.musicplayer.service.NotificationService;
import com.example.musicplayer.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    /**
     * Tạo một thông báo mới (chủ yếu dùng nội bộ hoặc để test)
     */
//    @PostMapping
//    public ResponseEntity<NotificationResponse> createNotification(@RequestBody NotificationRequest request) {
//        User currentUser = SecurityUtil.getCurrentUser();
//        NotificationResponse response = notificationMapper.toNotificationResponse(notificationService
//                .createNotification(currentUser, request));
//        return ResponseEntity.ok(response);
//    }

    /**
     * Lấy danh sách thông báo có phân trang (mặc định page = 0, size = 10).
     * Khi gọi API này, tất cả thông báo trong trang đó sẽ được đánh dấu là đã đọc.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User currentUser = SecurityUtil.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        List<NotificationResponse> list = notificationService.getUserNotifications(currentUser, pageable);
        return ResponseEntity.ok(new ApiResponse<>(list));
    }


    /**
     * Lấy toàn bộ thông báo không phân trang.
     * Khi gọi API này, tất cả sẽ được đánh dấu là đã đọc.
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAllNotifications() {
        User currentUser = SecurityUtil.getCurrentUser();
        List<NotificationResponse> notifications = notificationService.getAllUserNotifications(currentUser);
        return ResponseEntity.ok(new ApiResponse<>(notifications));
    }

    /**
     * Đánh dấu một thông báo cụ thể là đã đọc.
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        User currentUser = SecurityUtil.getCurrentUser();
        notificationService.markAsRead(id, currentUser);
        return ResponseEntity.ok(new ApiResponse<>(null));
    }

    /**
     * Đánh dấu toàn bộ thông báo là đã đọc.
     */
    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        User currentUser = SecurityUtil.getCurrentUser();
        notificationService.markAllAsRead(currentUser);
        return ResponseEntity.ok(new ApiResponse<>(null));
    }

    /**
     * Đếm số lượng thông báo chưa đọc của người dùng.
     */
    @GetMapping("/count-unread")
    public ResponseEntity<ApiResponse<Long>> countUnreadNotifications() {
        User currentUser = SecurityUtil.getCurrentUser();
        long count = notificationService.countUnreadNotifications(currentUser);
        return ResponseEntity.ok(new ApiResponse<>(count));
    }
}


