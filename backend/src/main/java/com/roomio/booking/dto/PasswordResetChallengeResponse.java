package com.roomio.booking.dto;

import java.time.Instant;

public record PasswordResetChallengeResponse(String message, String email, Instant expiresAt) {
}
