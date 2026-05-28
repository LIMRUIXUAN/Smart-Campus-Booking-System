package com.roomio.booking.dto;

import com.roomio.booking.model.BookingStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull BookingStatus status) {
}
