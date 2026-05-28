package com.roomio.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record BookingRequest(
    @NotBlank String resourceId,
    @NotBlank String eventName,
    @Min(1) Integer pax,
    @NotNull LocalDate startDate,
    @NotNull LocalTime startTime,
    @NotNull LocalDate endDate,
    @NotNull LocalTime endTime) {
}
