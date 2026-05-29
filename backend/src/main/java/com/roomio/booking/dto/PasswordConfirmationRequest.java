package com.roomio.booking.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordConfirmationRequest(@NotBlank String currentPassword) {
}
