package com.roomio.booking.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record SuggestionResponse(
    String resourceId,
    String resourceName,
    String location,
    Integer capacity,
    LocalDate date,
    LocalDate startDate,
    LocalDate endDate,
    LocalTime startTime,
    LocalTime endTime,
    String label,
    String reason) {
}
