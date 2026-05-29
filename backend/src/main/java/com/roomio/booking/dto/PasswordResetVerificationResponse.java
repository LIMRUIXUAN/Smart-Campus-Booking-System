package com.roomio.booking.dto;

import java.time.Instant;

public record PasswordResetVerificationResponse(String message, String token, Instant expiresAt) {
}
