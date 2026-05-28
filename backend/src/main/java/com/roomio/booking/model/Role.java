package com.roomio.booking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role implements EnumValue {
  STUDENT("student"),
  ADMIN("admin");

  private final String value;

  Role(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static Role fromValue(String value) {
    return EnumParsers.parse(Role.class, value);
  }
}
