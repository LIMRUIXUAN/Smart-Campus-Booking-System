package com.roomio.booking.dto;

import java.util.List;

public record AvailabilityResponse(
    boolean available,
    String reason,
    String type,
    BookingResponse conflict,
    List<SuggestionResponse> suggestions) {
}
