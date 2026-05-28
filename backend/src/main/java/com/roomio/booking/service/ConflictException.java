package com.roomio.booking.service;

import com.roomio.booking.dto.AvailabilityResponse;

public class ConflictException extends RuntimeException {
  private final AvailabilityResponse availability;

  public ConflictException(AvailabilityResponse availability) {
    super(availability.reason());
    this.availability = availability;
  }

  public AvailabilityResponse getAvailability() {
    return availability;
  }
}
