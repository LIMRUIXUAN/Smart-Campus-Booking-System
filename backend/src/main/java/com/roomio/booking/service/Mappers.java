package com.roomio.booking.service;

import com.roomio.booking.dto.BookingResponse;
import com.roomio.booking.dto.ResourceResponse;
import com.roomio.booking.dto.UserResponse;
import com.roomio.booking.model.Booking;
import com.roomio.booking.model.Resource;
import com.roomio.booking.model.User;

public final class Mappers {
  private Mappers() {
  }

  public static UserResponse user(User user) {
    return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
  }

  public static ResourceResponse resource(Resource resource) {
    return new ResourceResponse(
      resource.getId(),
      resource.getName(),
      resource.getType(),
      resource.getLocation(),
      resource.getCapacity(),
      resource.getStatus(),
      resource.getDescription(),
      resource.getImageUrl(),
      resource.getFeatures());
  }

  public static BookingResponse booking(Booking booking) {
    return new BookingResponse(
      booking.getId(),
      booking.getUser().getId(),
      booking.getUser().getName(),
      booking.getResource().getId(),
      booking.getEventName(),
      booking.getPax(),
      booking.getStartDate(),
      booking.getStartDate(),
      booking.getEndDate(),
      booking.getStartTime(),
      booking.getEndTime(),
      booking.getStartDateTime(),
      booking.getEndDateTime(),
      booking.getStatus());
  }
}
