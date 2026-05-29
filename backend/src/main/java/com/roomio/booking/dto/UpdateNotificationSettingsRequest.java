package com.roomio.booking.dto;

public record UpdateNotificationSettingsRequest(
    boolean bookingAlertsEnabled,
    boolean emailDigestEnabled,
    boolean pushNotificationsEnabled) {
}
