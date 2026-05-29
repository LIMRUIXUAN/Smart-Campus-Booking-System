package com.roomio.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetConfirmRequest(
    @NotBlank String token,
    @Size(min = 6) String newPassword) {
}
