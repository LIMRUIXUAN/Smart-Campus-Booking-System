package com.roomio.booking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResourceStatus implements EnumValue {
  ACTIVE("active"),
  INACTIVE("inactive");

  private final String value;

  ResourceStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static ResourceStatus fromValue(String value) {
    return EnumParsers.parse(ResourceStatus.class, value);
  }
}
