package com.example.musicplayer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "INVALID_INPUT")
    private String oldPassword;

    @NotBlank(message = "INVALID_INPUT")
    private String newPassword;
}