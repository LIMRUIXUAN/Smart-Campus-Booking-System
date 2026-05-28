package com.roomio.booking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BookingStatus implements EnumValue {
  CONFIRMED("confirmed"),
  CANCELLED("cancelled"),
  COMPLETED("completed"),
  NO_SHOW("no-show");

  private final String value;

  BookingStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static BookingStatus fromValue(String value) {
    return EnumParsers.parse(BookingStatus.class, value);
  }
}
