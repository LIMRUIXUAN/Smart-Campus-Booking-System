package com.roomio.booking.dto;

public record AuthResponse(UserResponse user, String token) {
}
