package com.roomio.booking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResourceType implements EnumValue {
  ROOM("Room"),
  LAB("Lab"),
  EQUIPMENT("Equipment");

  private final String value;

  ResourceType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static ResourceType fromValue(String value) {
    return EnumParsers.parse(ResourceType.class, value);
  }
}
