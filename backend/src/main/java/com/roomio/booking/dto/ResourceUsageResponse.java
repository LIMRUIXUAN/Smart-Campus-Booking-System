package com.roomio.booking.dto;

public record ResourceUsageResponse(String resourceId, String resourceName, long bookings) {
}
