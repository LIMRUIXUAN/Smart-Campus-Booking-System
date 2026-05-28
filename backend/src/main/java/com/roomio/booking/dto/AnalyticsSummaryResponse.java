package com.roomio.booking.dto;

public record AnalyticsSummaryResponse(
    long totalBookings,
    long activeBookings,
    long cancelledBookings,
    long noShowBookings,
    String mostBookedResource,
    String peakHour) {
}
