package com.roomio.booking.dto;

import com.roomio.booking.model.Role;

public record UserResponse(String id, String name, String email, Role role) {
}
