package com.roomio.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetCodeVerifyRequest(
    @Email @NotBlank String email,
    @NotBlank String code) {
}
