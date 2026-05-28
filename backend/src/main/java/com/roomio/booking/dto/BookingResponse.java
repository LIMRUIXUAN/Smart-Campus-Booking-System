package com.roomio.booking.dto;

import com.roomio.booking.model.BookingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BookingResponse(
    String id,
    String userId,
    String userName,
    String resourceId,
    String eventName,
    Integer pax,
    LocalDate date,
    LocalDate startDate,
    LocalDate endDate,
    LocalTime startTime,
    LocalTime endTime,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    BookingStatus status) {
}
