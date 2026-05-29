package com.roomio.booking.dto;

import jakarta.validation.constraints.NotBlank;

public record CodeConfirmationRequest(@NotBlank String code) {
}
