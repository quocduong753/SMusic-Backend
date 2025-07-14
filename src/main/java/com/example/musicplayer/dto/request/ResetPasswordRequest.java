package com.example.musicplayer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "INVALID_INPUT")
    private String otp;

    @NotBlank(message = "INVALID_INPUT")
    private String newPassword;
}
