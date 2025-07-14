package com.example.musicplayer.service;

import com.example.musicplayer.dto.request.CreatePaymentRequest;
import com.example.musicplayer.dto.request.NotificationRequest;
import com.example.musicplayer.dto.response.PaymentTransactionResponse;
import com.example.musicplayer.dto.response.UserSubscriptionResponse;
import com.example.musicplayer.enums.NotificationTargetType;
import com.example.musicplayer.enums.NotificationType;
import com.example.musicplayer.enums.PaymentStatus;
import com.example.musicplayer.exception.AppException;
import com.example.musicplayer.exception.ErrorCode;
import com.example.musicplayer.mapper.PaymentTransactionMapper;
import com.example.musicplayer.mapper.UserSubscriptionMapper;
import com.example.musicplayer.model.*;
import com.example.musicplayer.repository.PaymentTransactionRepository;
import com.example.musicplayer.repository.SubscriptionPlanRepository;
import com.example.musicplayer.repository.UserRepository;
import com.example.musicplayer.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentTransactionService {

    private final PaymentTransactionRepository transactionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final PaymentTransactionMapper transactionMapper;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserSubscriptionMapper userSubscriptionMapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public PaymentTransactionResponse createTransaction(CreatePaymentRequest request, User currentUser) {
        // 1. Kiểm tra gói VIP có tồn tại không
        SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        // 2. Tạo giao dịch mới
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setUser(currentUser);
        transaction.setPlan(plan);
        transaction.setAmount(plan.getPrice());
        transaction.setStatus(PaymentStatus.PENDING);
        transaction.setProvider(request.getProvider());
        transaction.setTransactionCode(UUID.randomUUID().toString()); // Có thể sửa sau
        transaction.setCreatedAt(LocalDateTime.now());

        // 3. Lưu vào DB
        transactionRepository.save(transaction);

        // 4. Trả về response
        return transactionMapper.toResponse(transaction);
    }

    @Transactional
    public UserSubscriptionResponse confirmTransaction(Long transactionId, User currentUser) {
        // 1. Tìm giao dịch
        PaymentTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        // 2. Kiểm tra quyền sở hữu
        if (!transaction.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        // 3. Kiểm tra trạng thái
        if (transaction.getStatus() != PaymentStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        // 4. Cập nhật giao dịch
        transaction.setStatus(PaymentStatus.SUCCESS);
        transaction.setConfirmedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        // 5. Tính thời hạn VIP
        SubscriptionPlan plan = transaction.getPlan();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startAt = now;
        LocalDateTime endAt = now.plusDays(plan.getDurationInDays());

        // 6. Nếu user có gói VIP còn hạn → cộng dồn
        Optional<UserSubscription> latestSubOpt = userSubscriptionRepository
                .findTopByUserOrderByEndAtDesc(currentUser);

        if (latestSubOpt.isPresent() && latestSubOpt.get().getEndAt().isAfter(now)) {
            startAt = latestSubOpt.get().getEndAt();
            endAt = startAt.plusDays(plan.getDurationInDays());
        }

        // 7. Tạo UserSubscription
        UserSubscription subscription = new UserSubscription();
        subscription.setUser(currentUser);
        subscription.setPlan(plan);
        subscription.setTransaction(transaction);
        subscription.setStartAt(startAt);
        subscription.setEndAt(endAt);
        userSubscriptionRepository.save(subscription);

        if (!currentUser.isVip()) {
            currentUser.setVip(true);
            userRepository.save(currentUser);
        }
        userRepository.save(currentUser);

        // 8. Gửi thông báo chúc mừng VIP
        NotificationRequest request = NotificationRequest.builder()
                .title("Chúc mừng bạn đã nâng cấp VIP 🎉")
                .message("Bạn đã đăng ký gói VIP thành công. Hãy khám phá những đặc quyền mới ngay!")
                .type(NotificationType.VIP_PURCHASE)
                .targetType(NotificationTargetType.VIP)
                .targetId(plan.getId())
                .build();
        notificationService.createNotification(currentUser, request);

        // 9. Trả về response bằng MapStruct
        return userSubscriptionMapper.toResponse(subscription);
    }

}