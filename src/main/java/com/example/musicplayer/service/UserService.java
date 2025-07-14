package com.example.musicplayer.service;

import com.example.musicplayer.dto.request.ChangePasswordRequest;
import com.example.musicplayer.dto.request.LoginRequest;
import com.example.musicplayer.dto.request.RegisterRequest;
import com.example.musicplayer.dto.request.UpdateUserRequest;
import com.example.musicplayer.dto.response.AuthResponse;
import com.example.musicplayer.dto.response.UserResponse;
import com.example.musicplayer.enums.UserRole;
import com.example.musicplayer.exception.AppException;
import com.example.musicplayer.exception.ErrorCode;
import com.example.musicplayer.mapper.UserMapper;
import com.example.musicplayer.model.PasswordResetToken;
import com.example.musicplayer.model.User;
import com.example.musicplayer.repository.PasswordResetTokenRepository;
import com.example.musicplayer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);

        String token = jwtService.generateToken(user);
        UserResponse userResponse = userMapper.toUserResponse(user);

        return new AuthResponse(token, userResponse);
    }

    public User authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        return user;
    }

    public AuthResponse login(LoginRequest request) {
        User user = authenticate(request);
        if (user.getRole() != UserRole.USER) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String token = jwtService.generateToken(user);
        UserResponse userResponse = userMapper.toUserResponse(user);
        return new AuthResponse(token, userResponse);
    }

    public AuthResponse loginAdmin(LoginRequest request) {
        User admin = authenticate(request);
        if (admin.getRole() != UserRole.ADMIN) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String token = jwtService.generateToken(admin);
        UserResponse userResponse = userMapper.toUserResponse(admin);
        return new AuthResponse(token, userResponse);
    }

    public UserResponse updateProfile(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        userMapper.updateUserFromUserUpdateRequest(request, user);

        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    public UserResponse getUserInfo(User user) {
        return userMapper.toUserResponse(user);
    }

    public void changePassword(User currentUser, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }

    public void sendOtpToEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        // Ghi đè nếu đã có
        PasswordResetToken token = passwordResetTokenRepository.findByUser(user)
                .orElse(new PasswordResetToken());

        token.setUser(user);
        token.setOtp(otp);
        token.setExpiryDate(expiry);
        passwordResetTokenRepository.save(token);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    public void resetPasswordWithOtp(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        PasswordResetToken token = passwordResetTokenRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));

        if (!token.getOtp().equals(otp)) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(token); // Xóa sau khi dùng
    }

}

