package com.roomio.booking.dto;

import java.time.Instant;

public record VerificationChallengeResponse(String message, String code, Instant expiresAt) {
}
